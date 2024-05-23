package com.nombreGrupo.restControllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nombreGrupo.modelo.entities.Categoria;
import com.nombreGrupo.modelo.entities.Producto;
import com.nombreGrupo.services.CategoriaService;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaRestController {

	@Autowired
    private CategoriaService categoriaService;
	
	/* LECTURA----------------------------------------------------------------*/
	
    @GetMapping("/nombre/{nombreCategoria}")
    public ResponseEntity<?> getShowPorNombre(@PathVariable String nombreCategoria) {
        try {
            Categoria categoria = categoriaService.encontrarPorNombre(nombreCategoria);
            return ResponseEntity.ok(categoria);
        } catch (EntityNotFoundException ex) {
        	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", ex.getMessage()));
        }
    }
    
    @GetMapping("/{id}/productos")
    public ResponseEntity<List<Producto>> getProductosPorCategoria(@PathVariable("id") Integer id) {
        List<Producto> productos = categoriaService.encontrarProductosPorSubcategoriaCategoriaIdCategoria(id);
        return ResponseEntity.ok(productos);
    }
	
}
