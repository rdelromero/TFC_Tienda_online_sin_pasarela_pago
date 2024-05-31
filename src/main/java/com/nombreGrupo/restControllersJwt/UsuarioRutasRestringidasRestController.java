package com.nombreGrupo.restControllersJwt;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nombreGrupo.modelo.dto.PedidoDtoActualizacionSinCambiarLineasFacturacion;
import com.nombreGrupo.modelo.dto.PedidoDtoCreacionConLineasFacturacion;
import com.nombreGrupo.modelo.dto.ResenaDtoCreacion;
import com.nombreGrupo.modelo.dto.UsuarioDtoRegistro;
import com.nombreGrupo.modelo.entities.Pedido;
import com.nombreGrupo.modelo.entities.Usuario;
import com.nombreGrupo.seguridad.JwtListaNegraService;
import com.nombreGrupo.seguridad.JwtUtil;
import com.nombreGrupo.services.PedidoService;
import com.nombreGrupo.services.ResenaService;
import com.nombreGrupo.services.UsuarioService;
import com.nombreGrupo.util.EmailUtil;

import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/apiprivada")
public class UsuarioRutasRestringidasRestController {
    
	@Autowired
    private JwtListaNegraService tokenListaNegraService;
	@Autowired
    private UsuarioService usuarioService;
	@Autowired
    private PedidoService pedidoService;
	@Autowired
    private ResenaService resenaService;
	@Autowired
    private JwtUtil jwtUtil;
	@Autowired
    private EmailUtil emailUtil;

    @GetMapping("/datos-usuario")
    public ResponseEntity<?> getMiCuenta(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader.substring(7);
        int idUsuario = jwtUtil.extraerIdUsuario(token);

        try {
            Usuario usuario = usuarioService.encontrarPorId(idUsuario);
            return ResponseEntity.ok(usuario);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", ex.getMessage()));
        }
    }
    
    @GetMapping("/pedidos")
    public ResponseEntity<?> getPedidos(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader.substring(7);
        int idUsuario = jwtUtil.extraerIdUsuario(token);

        try {
            List<Pedido> pedidos = pedidoService.encontrarPorUsuario_IdUsuario(idUsuario);
            return ResponseEntity.ok(pedidos);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", ex.getMessage()));
        }
    }
	
    /* ACTUALIZAR ---------------------------------------------------------------*/
    @PutMapping("/actualizar-datos-usuario")
    public ResponseEntity<?> putUpdate(@RequestBody UsuarioDtoRegistro usuarioDtoActualizacion, HttpServletRequest request) {
    	
    	String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader.substring(7);
        int idUsuario = jwtUtil.extraerIdUsuario(token);

        try {
            usuarioService.actualizar(idUsuario, usuarioDtoActualizacion);
            return ResponseEntity.ok(usuarioDtoActualizacion);
        } catch (EntityNotFoundException ex) {
            // No existe usuario con ese idUsuario
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", ex.getMessage()));
        } catch (DataIntegrityViolationException ex) {
            // Existe otro usuario con la direccionEmail que se propone
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("mensaje", ex.getMessage()));
        } catch (Exception ex) {
            // Logging del error
        	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("mensaje", ex.getMessage()));
        }
    }
    
    @PostMapping("/resenas")
    public ResponseEntity<?> postCrearYGuardar(@RequestBody ResenaDtoCreacion resenaDtoCreacion, HttpServletRequest request) {
        
    	String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader.substring(7);
        int idUsuario = jwtUtil.extraerIdUsuario(token);
        resenaDtoCreacion.setIdUsuario(idUsuario);
    	try {
    		resenaService.crearYGuardar(resenaDtoCreacion);
    		//NO DEJA RETORNAR LA RESEÑA CREADA, así que devuelvo resenaDtoCreacion
            return ResponseEntity.ok(resenaDtoCreacion);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", ex.getMessage()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", ex.getMessage()));
        }
    }
    
    @PostMapping
    public String postBienvenida()
    {
        return "Has accedido a una zona privada.";
    }
    
    @PostMapping("/logout")
    public Map<String, String> postLogout(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        tokenListaNegraService.blacklistToken(token);
        return Map.of("Mensaje", "Logout exitoso");
    }
    
    @PostMapping("/pedidos")
    public ResponseEntity<?> postCrearYGuardar(@RequestBody PedidoDtoCreacionConLineasFacturacion pedidoDtoCreacionConLF, HttpServletRequest request) {
        
    	String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader.substring(7);
        int idUsuario = jwtUtil.extraerIdUsuario(token);
        pedidoDtoCreacionConLF.setIdUsuario(idUsuario);
    	try {
        	Pedido pedidoGuardado = pedidoService.crearYGuardarConLF(pedidoDtoCreacionConLF);

            new Thread(() -> {
                try {
                    Pedido pedidoGuardado2 = pedidoService.encontrarPorId(pedidoGuardado.getIdPedido());
                    emailUtil.enviarEmailPedido(pedidoGuardado2.getUsuario().getNombre(), pedidoGuardado2.getUsuario().getUsername(), pedidoGuardado2);
                
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }).start();
            //NO DEJA RETORNAR EL PEDIDO QUE SE HA CREADO (cualquier otro pedido sí), así que devuelvo un pedidoDtoCreacionConLF
            return ResponseEntity.ok(pedidoDtoCreacionConLF);

        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", ex.getMessage()));
        } catch (IllegalArgumentException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", ex.getMessage()));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("mensaje", ex.getMessage()));
        }
    }
    
    @PutMapping("/pedidos/{idPedido}")
    public ResponseEntity<?> putActualizar(@PathVariable int idPedido, @RequestBody PedidoDtoActualizacionSinCambiarLineasFacturacion pedidoDtoActualizacionSinCambiarLF, HttpServletRequest request) {
        
    	String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader.substring(7);
        int idUsuarioDelJwt = jwtUtil.extraerIdUsuario(token);
        
    	try {
        	Pedido pedidoGuardado = pedidoService.actualizarSinCambiarLFUsuario(idUsuarioDelJwt, idPedido, pedidoDtoActualizacionSinCambiarLF);
            return ResponseEntity.ok(pedidoGuardado);

        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", ex.getMessage()));
        } catch (IllegalArgumentException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", ex.getMessage()));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("mensaje", ex.getMessage()));
        }
    }
}
