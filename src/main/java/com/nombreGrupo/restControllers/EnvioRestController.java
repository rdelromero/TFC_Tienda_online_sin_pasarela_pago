package com.nombreGrupo.restControllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.nombreGrupo.modelo.dto.EnvioDtoActualizacion;
import com.nombreGrupo.modelo.dto.EnvioDtoCreacion;
import com.nombreGrupo.modelo.entities.Envio;
import com.nombreGrupo.modelo.entities.Pedido;
import com.nombreGrupo.services.EnvioService;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/pedidos/envios")
public class EnvioRestController {

    @Autowired
    private EnvioService envioService;
    
    @GetMapping
    public ResponseEntity<List<Envio>> getEncontrarTodos() {
        List<Envio> pedidos = envioService.encontrarTodos();
        return ResponseEntity.ok(pedidos);
    }
    
    @GetMapping("/{idEnvio}")
    public ResponseEntity<?> getEncontrarPorId(@PathVariable int idEnvio) {
    	try {
            Envio envio = envioService.encontrarPorId(idEnvio);
            return ResponseEntity.ok(envio);
    	} catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", ex.getMessage()));
        } catch (Exception ex) {
        	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("mensaje", "Los datos del envío no han podido ser cargados."));
        }
    }
    
	@PostMapping
	public ResponseEntity<?> postCrearYGuardar(@RequestBody EnvioDtoCreacion envioDtoCreacion) {
        try {
            Envio envio = envioService.crearYGuardar(envioDtoCreacion);
            return ResponseEntity.ok(envio);
        } catch (Exception ex) {
        	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("mensaje", "El envío no ha sido creado."));
        }
	}
	
	@PutMapping("/{idEnvio}")
    public ResponseEntity<?> putActualizar(@PathVariable int idEnvio, @RequestBody EnvioDtoActualizacion envioDtoActualizacion) {
        try {
            Envio envio = envioService.actualizar(idEnvio, envioDtoActualizacion);
            return ResponseEntity.ok(envio);
        } catch (EntityNotFoundException ex) {
        	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", ex.getMessage()));
        } catch (Exception ex) {
        	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("mensaje", "El envío no ha sido actualizado."));
        }
	}
	
}