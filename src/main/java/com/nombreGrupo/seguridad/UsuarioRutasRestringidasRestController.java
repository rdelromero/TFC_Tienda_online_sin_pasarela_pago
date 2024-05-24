package com.nombreGrupo.seguridad;

import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nombreGrupo.modelo.dto.LineaFacturacionDto;
import com.nombreGrupo.modelo.dto.PedidoDtoCreacionConLineasFacturacion;
import com.nombreGrupo.modelo.dto.PedidoDtoCreacionConLineasFacturacionSinIdUsuario;
import com.nombreGrupo.modelo.entities.LineaFacturacion;
import com.nombreGrupo.modelo.entities.Pedido;
import com.nombreGrupo.modelo.entities.Producto;
import com.nombreGrupo.modelo.entities.Usuario;
import com.nombreGrupo.services.PedidoService;
import com.nombreGrupo.util.EmailUtil;

import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/apiprivada")
public class UsuarioRutasRestringidasRestController {
    
	@Autowired
    private JwtListaNegraService tokenListaNegraService;
	@Autowired
    private PedidoService pedidoService;
	@Autowired
    private JwtUtil jwtUtil;
	@Autowired
    private EmailUtil emailUtil;

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
    
    @PostMapping("pedidos")
    public ResponseEntity<?> postCrearYGuardar(@RequestBody PedidoDtoCreacionConLineasFacturacion pedidoDtoCreacionConLF, HttpServletRequest request) {
        
    	String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader.substring(7);
        int idUsuario = jwtUtil.extraerIdUsuario(token);
        pedidoDtoCreacionConLF.setIdUsuario(idUsuario);
    	try {
        	
        
        	Pedido pedidoGuardado = pedidoService.crearYGuardarConLF(pedidoDtoCreacionConLF);

            // Retornar la respuesta inmediatamente
            ResponseEntity<?> response = ResponseEntity.ok(pedidoGuardado);

            // Ejecutar el envío de email después de retornar la respuesta
            new Thread(() -> {
                try {
                    Pedido pedidoGuardado2 = pedidoService.encontrarPorId(pedidoGuardado.getIdPedido());
                    emailUtil.enviarEmailPedido(pedidoGuardado2.getUsuario().getNombre(), pedidoGuardado2.getUsuario().getUsername(), pedidoGuardado2);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }).start();

            return response;

        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", ex.getMessage()));
        } catch (IllegalArgumentException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", ex.getMessage()));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("mensaje", ex.getMessage()));
        }
    }
    
}
