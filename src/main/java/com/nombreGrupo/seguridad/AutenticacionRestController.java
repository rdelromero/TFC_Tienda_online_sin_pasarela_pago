package com.nombreGrupo.seguridad;

import com.nombreGrupo.modelo.dto.UsuarioDtoLogin;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api")
public class AutenticacionRestController {

    @Autowired
    private UsuarioAutenticacionService usuarioAutenticacionService;
    
    @PostMapping("/login")
    public ResponseEntity<?> postLoginRetornandoJwt(@RequestBody UsuarioDtoLogin usuarioDtoLogin) {
        try {
            String jwt = usuarioAutenticacionService.autenticarYRetornarJWT(usuarioDtoLogin);
            return ResponseEntity.ok(Map.of("JWT", jwt));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("Mensaje", ex.getMessage()));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("Mensaje", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("Mensaje", "Contraseña incorrecta"));
        }
    }
}