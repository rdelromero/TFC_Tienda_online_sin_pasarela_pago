package com.nombreGrupo.restControllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nombreGrupo.modelo.entities.Resena;
import com.nombreGrupo.services.ResenaService;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/resenas")
public class ResenaRestController {

    @Autowired
    private ResenaService resenaService;
	
    /* LECTURA----------------------------------------------------------------*/
    @GetMapping
    public ResponseEntity<List<Resena>> getIndex() {
        List<Resena> resenas = resenaService.encontrarTodas();
        return ResponseEntity.ok(resenas);
    }
	
    /* BORRADO-----------------------------------------------------------------------*/
    @DeleteMapping("/{idResena}")
    public ResponseEntity<?> destroyPorId(@PathVariable int idResena) {
       try {
    	   resenaService.borrarPorId(idResena);
    	   return ResponseEntity.ok(Map.of("mensaje", "Resena de idResena "+idResena+" borrada correctamente."));
       } catch (EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", ex.getMessage()));
	   }
    }
}
