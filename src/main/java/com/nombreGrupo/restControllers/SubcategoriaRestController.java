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

import com.nombreGrupo.modelo.entities.Producto;
import com.nombreGrupo.modelo.entities.Subcategoria;
import com.nombreGrupo.services.ProductoService;
import com.nombreGrupo.services.SubcategoriaService;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/subcategorias")
public class SubcategoriaRestController {

	@Autowired
    private SubcategoriaService subcategoriaService;
	
	/* LECTURA----------------------------------------------------------------*/
	
    @GetMapping("/nombre/{nombreSubcategoria}")
    public ResponseEntity<?> getShowPorNombre(@PathVariable String nombreSubcategoria) {
        try {
            Subcategoria subcategoria = subcategoriaService.encontrarPorNombre(nombreSubcategoria);
            return ResponseEntity.ok(subcategoria);
        } catch (EntityNotFoundException ex) {
        	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", ex.getMessage()));
        }
    }

    @GetMapping("/{idSubcategoria}/productos")
    public ResponseEntity<List<Producto>> getProductosPorSubcategoria_IdSubcategoria(@PathVariable int idSubcategoria) {
        List<Producto> productos = subcategoriaService.encontrarProductosPorSubcategoria_IdSubcategoria(idSubcategoria);
        return ResponseEntity.ok(productos);
    }
}
