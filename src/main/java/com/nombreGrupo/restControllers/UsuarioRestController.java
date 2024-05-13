package com.nombreGrupo.restControllers;

import com.nombreGrupo.modelo.entities.Pedido;
import com.nombreGrupo.modelo.entities.Usuario;
import com.nombreGrupo.modelo.dto.UsuarioDtoRegistro;
import com.nombreGrupo.modelo.dto.UsuarioDtoLogin;
import com.nombreGrupo.services.PedidoService;
import com.nombreGrupo.services.UsuarioServiceImplMy8;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioRestController {

    @Autowired
    private UsuarioServiceImplMy8 usuarioService;

    @Autowired
    private PedidoService pedidoService;
    
    /* LECTURA------------------------------------------------------*/
    @GetMapping
    public ResponseEntity<List<Usuario>> getAllUsuarios() {
        List<Usuario> usuarios = usuarioService.encontrarTodos();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/verificados")
    public ResponseEntity<List<Usuario>> getUsuariosVerificados() {
        List<Usuario> usuarios = usuarioService.encontrarPorActiveTrue();
        return ResponseEntity.ok(usuarios);
    }
    
    // Obtener un usuario por ID
    @GetMapping("/{idUsuario}")
    public ResponseEntity<?> getUsuarioById(@PathVariable int idUsuario) {
        try {
            Usuario usuario = usuarioService.encontrarPorId(idUsuario);
            return ResponseEntity.ok(usuario);
        } catch (EntityNotFoundException ex) {
            // Capturar y manejar la excepción específica si el usuario no se encuentra
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", ex.getMessage()));
        } catch (Exception ex) {
        // Capturar cualquier otra excepción que pueda surgir durante la ejecución
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("mensaje", "Error interno del servidor. Por favor, intente de nuevo más tarde."));
        }
    }

    /* CREACIÓN----------------------------------------------------------*/
    @PostMapping
    public ResponseEntity<?> postRegistrarUsuario(@RequestBody UsuarioDtoRegistro registroDto) {
        try {
            Usuario usuarioGuardado = usuarioService.crearYGuardar(registroDto);
            return ResponseEntity.ok(usuarioGuardado);
        } catch (IllegalArgumentException ex) {
        	// Formato de direccionEmail incorrecto
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("mensaje", ex.getMessage()));
        } catch ( DataIntegrityViolationException ex) {
        	// Ya existe un usuario con esa direccionEmail
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("mensaje", ex.getMessage()));
        }
    }

    @PutMapping("/no-verificados/verificar-cuenta")
    public ResponseEntity<?> PutVerificarCuenta(@RequestParam String direccionEmail, @RequestParam String otp) {
        try {
            Usuario usuarioGuardado = usuarioService.verificarCuentaPorDireccionEmailYOpt(direccionEmail, otp);
            return ResponseEntity.ok(usuarioGuardado);
        } catch (EntityNotFoundException ex) {
            // No existe usuario con esa direccionEmail
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", ex.getMessage()));
        } catch (IllegalStateException ex) {
            // Captura la excepción cuando la cuenta ya está verificada
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("mensaje", ex.getMessage()));
        } catch (IllegalArgumentException ex) {
            // Captura errores como OTP incorrecto o caducado
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("mensaje", ex.getMessage()));
        } catch (RuntimeException ex) {
            // Captura otros errores de tiempo de ejecución
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("mensaje", "Error interno del servidor. Por favor, intente de nuevo más tarde."));
        }
    }
    
    @PutMapping("/no-verificados/{idUsuario}/regenerar-otp")
    public ResponseEntity<?> regenerarOtpParaUsuarioNoVerificado(@PathVariable("idUsuario") int idUsuario) {
        try {
            Usuario usuarioGuardado = usuarioService.regenerarOtpParaUsuarioNoVerificado(idUsuario);
            return ResponseEntity.ok(usuarioGuardado);
        } catch (EntityNotFoundException ex) {
        	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", ex.getMessage()));
        } catch (IllegalStateException ex) {
            // Captura la excepción cuando la cuenta ya está verificada
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("mensaje", ex.getMessage()));
        } catch (IllegalArgumentException ex) {
            // direccionEmail no tiene el formato adecuado
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("mensaje", ex.getMessage()));
        }
    }
    
    /* ACTUALIZAR ---------------------------------------------------------------*/
    @PutMapping("/{idUsuario}/actualizar")
    public ResponseEntity<?> actualizarUsuario(@PathVariable int idUsuario, @RequestBody UsuarioDtoRegistro usuarioRegistroDto) {
        try {
            Usuario usuarioActualizado = usuarioService.actualizar(idUsuario, usuarioRegistroDto);
            return ResponseEntity.ok(usuarioActualizado);
        } catch (EntityNotFoundException ex) {
            // No existe usuario con ese idUsuario
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", ex.getMessage()));
        } catch (IllegalStateException ex) {
            // La cuenta de usuario no está verificada
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("mensaje", ex.getMessage()));
        } catch (DataIntegrityViolationException ex) {
            // Existe otro usuario con la direccionEmail que se propone
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("mensaje", ex.getMessage()));
        } catch (Exception ex) {
            // Logging del error
        	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("mensaje", ex.getMessage()));
        }
    }
    
    /* BORRADO-----------------------------------------------------------------------*/
    @DeleteMapping("/{idUsuario}")
    public ResponseEntity<?> deleteUsuario(@PathVariable int idUsuario) {
        boolean isDeleted = usuarioService.borrarPorId(idUsuario);
        if (isDeleted) {
            return ResponseEntity.ok(Map.of("mensaje", "Usuario borrado correctamente."));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", "No se ha podido borrar. No existe autor con ese id."));
        }
    }
    
    /* LOGIN----------------------------------------------------------------------*/
    @PutMapping("/login")
    public ResponseEntity<?> login(@RequestBody UsuarioDtoLogin usuarioDtoLogin) {
    	try {
    		return ResponseEntity.ok(usuarioService.loguearse(usuarioDtoLogin));
    	} catch (EntityNotFoundException ex) {
    		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", ex.getMessage()));
        } catch (IllegalStateException ex) {
      	  return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", ex.getMessage()));
        } catch (IllegalArgumentException ex) {
        	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("mensaje", ex.getMessage()));
        }
    }

    @GetMapping("/{idUsuario}/pedidos")
    public ResponseEntity<?> getPedidosPorUsuario(@PathVariable int idUsuario) {
        List<Pedido> pedidos = pedidoService.encontrarPorUsuarioIdUsuario(idUsuario);
        if (pedidos.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", "El usuario de idUsuario "+idUsuario+" no tiene pedidos a su nombre."));
        }
        return ResponseEntity.ok(pedidos);
    }
    
}