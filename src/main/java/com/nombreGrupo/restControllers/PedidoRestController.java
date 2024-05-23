package com.nombreGrupo.restControllers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nombreGrupo.modelo.dto.PedidoDtoActualizacionSinCambiarLineasFacturacion;

import com.nombreGrupo.modelo.dto.PedidoDtoCreacionConLineasFacturacion;
import com.nombreGrupo.modelo.entities.Pedido;
import com.nombreGrupo.modelo.entities.Producto;
import com.nombreGrupo.modelo.entities.LineaFacturacion;
import com.nombreGrupo.modelo.entities.Usuario;
import com.nombreGrupo.modelo.entities.Pedido.EstadoPedido;
import com.nombreGrupo.services.PedidoService;
import com.nombreGrupo.services.UsuarioService;
import com.nombreGrupo.util.EmailUtil;

import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoRestController {

    @Autowired
    private PedidoService pedidoService;
    
	@Autowired
	private EmailUtil emailUtil;
    
    @GetMapping
    public ResponseEntity<List<Pedido>> getIndex() {
        List<Pedido> pedidos = pedidoService.encontrarTodos();
        return ResponseEntity.ok(pedidos);
    }
    
    @GetMapping("/{idPedido}")
    public ResponseEntity<?> getShowPorId(@PathVariable int idPedido) {
    	try {
            Pedido pedido = pedidoService.encontrarPorId(idPedido);
            return ResponseEntity.ok(pedido);
    	} catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", ex.getMessage()));
        } catch (Exception ex) {
        	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("mensaje", "Los datos del pedido no han podido ser cargados."));
        }
    }
    
    @GetMapping("/")
    public ResponseEntity<List<Pedido>> getIndexPorEstado(@RequestParam("estado") EstadoPedido estado) {
        List<Pedido> pedidos = pedidoService.encontrarPorEstado(estado);
        if (pedidos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{idPedido}/lineas-facturacion")
    public ResponseEntity<?> obtenerLineasFacturacion(@PathVariable int idPedido) {
    	
    	try {
            Pedido pedido = pedidoService.encontrarPorId(idPedido);
            return ResponseEntity.ok(pedido.getLineasFacturacion());
    	} catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", "No existe un pedido de idPedido "+idPedido+"."));
        }
    	

    }
    
    @PostMapping
    public ResponseEntity<?> postCrearYGuardar(@RequestBody PedidoDtoCreacionConLineasFacturacion pedidoDtoCreacionConLF) {
        try {
        	Pedido pedidoGuardado = pedidoService.crearYGuardarConLF(pedidoDtoCreacionConLF);

            // Retornar la respuesta inmediatamente
            ResponseEntity<?> response = ResponseEntity.ok(pedidoGuardado);

            // Ejecutar el envío de email después de retornar la respuesta
            new Thread(() -> {
                try {
                    Pedido pedidoGuardado2 = pedidoService.encontrarPorId(pedidoGuardado.getIdPedido());
                    emailUtil.enviarEmailPedido(pedidoGuardado2.getUsuario().getNombre(), pedidoGuardado2.getUsuario().getDireccionEmail(), pedidoGuardado2);
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
	
	@PutMapping("/{idPedido}")
    public ResponseEntity<?> putActualizar(@PathVariable int idPedido, @RequestBody PedidoDtoActualizacionSinCambiarLineasFacturacion pedidoDtoActualizacion) {
        try {
            Pedido pedidoGuardado = pedidoService.actualizarSinCambiarLF(idPedido, pedidoDtoActualizacion);
            return ResponseEntity.ok(pedidoGuardado);
        } catch (EntityNotFoundException ex) {
        	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", ex.getMessage()));
        } /*catch (Exception ex) {
        	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("mensaje", "El pedido no ha sido actualizado."));
        }*/
	}
	
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<?> getPedidosPorUsuario(@PathVariable int idUsuario) {
        List<Pedido> pedidos = pedidoService.encontrarPorUsuarioIdUsuario(idUsuario);
        if (pedidos.isEmpty()) {
        	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", "El usuario de idUsuario "+idUsuario+" no tiene pedidos a su nombre."));
        }
        return ResponseEntity.ok(pedidos);
    }
	
}
