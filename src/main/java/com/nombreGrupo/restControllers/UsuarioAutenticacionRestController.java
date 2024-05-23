package com.nombreGrupo.restControllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nombreGrupo.modelo.dto.UsuarioDtoLogin;
import com.nombreGrupo.services.UsuarioAutenticacionService;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api")
public class UsuarioAutenticacionRestController {

	@Autowired
    private UsuarioAutenticacionService usuarioAutenticacionService;

    /* LOGIN----------------------------------------------------------------------*/
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UsuarioDtoLogin usuarioDtoLogin) {
    	try {
    		return ResponseEntity.ok(usuarioAutenticacionService.loguearse(usuarioDtoLogin));
    	} catch (EntityNotFoundException ex) {
    		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", ex.getMessage()));
        } catch (IllegalStateException ex) {
      	  return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", ex.getMessage()));
        } catch (IllegalArgumentException ex) {
        	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("mensaje", ex.getMessage()));
        }
    }
}
