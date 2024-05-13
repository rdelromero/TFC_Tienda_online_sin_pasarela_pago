package com.nombreGrupo.restControllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nombreGrupo.modelo.dto.FabricanteDtoCreacion;
import com.nombreGrupo.modelo.dto.PedidoDtoActualizacionSinCambiarLineasFacturacion;
import com.nombreGrupo.modelo.dto.ProductoDtoCreacion;
import com.nombreGrupo.modelo.entities.Fabricante;
import com.nombreGrupo.modelo.entities.Pedido;
import com.nombreGrupo.modelo.entities.Producto;
import com.nombreGrupo.services.FabricanteService;
import com.nombreGrupo.services.ProductoService;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/fabricantes")
public class FabricanteRestController {
	
	@Autowired
    private FabricanteService fabricanteService;
	
	@Autowired
    private ProductoService productoService;
	
    @GetMapping
    public ResponseEntity<List<Fabricante>> listarTodosLosPedidos() {
        List<Fabricante> fabricantes = fabricanteService.encontrarTodos();
        return ResponseEntity.ok(fabricantes);
    }
    
    @GetMapping("/{idFabricante}/productos")
    public ResponseEntity<List<Producto>> getProductosPorFabricante_IdFabricante(@PathVariable int idFabricante) {
        List<Producto> productos = productoService.encontrarPorFabricante_IdFabricante(idFabricante);
        return ResponseEntity.ok(productos);
    }
    
    /* CREACIÓN------------------------------------------------------------------*/
	@PostMapping
	public ResponseEntity<?> postCrearYGuardar(@RequestBody FabricanteDtoCreacion fabricanteDtoCreacion) {
        Fabricante fabricanteGuardado = fabricanteService.crearYGuardar(fabricanteDtoCreacion);
        return ResponseEntity.ok(fabricanteGuardado);
	}
    
	/* ACTUALIZACIÓN------------------------------------------------------------------*/
	@PutMapping("/{idProducto}")
    public ResponseEntity<?> putActualizar(@PathVariable int idProducto, @RequestBody FabricanteDtoCreacion fabricanteDtoActualizacion) {
        try {
            Fabricante fabricanteGuardado = fabricanteService.actualizar(idProducto, fabricanteDtoActualizacion);
            return ResponseEntity.ok(fabricanteGuardado);
        } catch (EntityNotFoundException ex) {
        	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", ex.getMessage()));
        } catch (Exception ex) {
        	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("mensaje", "El pedido no ha sido actualizado."));
        }
	}
	
    /* BORRADO-----------------------------------------------------------------------*/
    @DeleteMapping("/{idFabricante}")
    public ResponseEntity<?> deletePorId(@PathVariable int idFabricante) {
       try {
    	   fabricanteService.borrarPorId(idFabricante);
    	   return ResponseEntity.ok(Map.of("mensaje", "Fabricante borrado correctamente."));
       } catch (EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", ex.getMessage()));
	   } catch (IllegalStateException ex) {
		   return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("mensaje", ex.getMessage()));
       }
    }
}
