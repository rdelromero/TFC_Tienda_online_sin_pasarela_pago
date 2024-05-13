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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nombreGrupo.modelo.dto.ProductoDtoCreacion;
import com.nombreGrupo.modelo.entities.Fabricante;
import com.nombreGrupo.modelo.entities.Pedido;
import com.nombreGrupo.modelo.entities.Producto;
import com.nombreGrupo.modelo.entities.Producto.TipoDescuento;
import com.nombreGrupo.modelo.entities.Pedido.EstadoPedido;
import com.nombreGrupo.services.ProductoService;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/productos")
public class ProductoRestController {

    @Autowired
    private ProductoService productoService;
    
    /* LECTURA----------------------------------------------------------------*/
    @GetMapping
    public ResponseEntity<List<Producto>> getEncontrarTodos() {
        List<Producto> productos = productoService.encontrarTodos();
        return ResponseEntity.ok(productos);
    }
    
    @GetMapping("/{idProducto}")
    public ResponseEntity<?> getPorId(@PathVariable int idProducto) {
    	try {
            Producto producto = productoService.encontrarPorId(idProducto);
            return ResponseEntity.ok(producto);
    	} catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", ex.getMessage()));
        } catch (Exception ex) {
        	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("mensaje", "Los datos del producto no han podido ser cargados."));
        }
    }
    
    @GetMapping("/subcategoria/{idSubcategoria}")
    public ResponseEntity<List<Producto>> getPorSubcategoria_IdSubcategoria(@PathVariable int idSubcategoria) {
        List<Producto> productos = productoService.encontrarPorSubcategoria_IdSubcategoria(idSubcategoria);
        if (productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productos);
    }
    
    @GetMapping("/fabricante/{idFabricante}")
    public ResponseEntity<List<Producto>> getPorFabricante_IdFabricante(@PathVariable int idFabricante) {
        List<Producto> productos = productoService.encontrarPorFabricante_IdFabricante(idFabricante);
        if (productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productos);
    }
    
    @GetMapping("/tipo-descuento/{tipoDescuento}")
    public ResponseEntity<List<Producto>> getPedidosPorTipoDescuento(@PathVariable TipoDescuento tipoDescuento) {
        List<Producto> productos = productoService.encontrarPorTipoDescuento(tipoDescuento);
        if (productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productos);
    }
    
    @GetMapping("/categoria/{idCategoria}")
    public ResponseEntity<List<Producto>> getPorIdCategoria(@PathVariable int idCategoria) {
        List<Producto> productos = productoService.encontrarPorSubcategoriaCategoriaIdCategoria(idCategoria);
        if (productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productos);
    }
    
    @GetMapping("/filtro")
    public ResponseEntity<List<Producto>> getPorSubcategoriaCategoriaIdCategoriaYFabricante_IdFabricanteYPrecioFinal(
        @RequestParam(value = "idCategoria", required = false) Integer idCategoria,
        @RequestParam(value = "tipoDescuento", required = false, defaultValue = "sin_descuento") TipoDescuento tipoDescuento,
        @RequestParam(value = "idFabricante", required = false) Integer idFabricante,
        @RequestParam(value = "precioFinalMinimo", required = false, defaultValue = "0") double precioFinalMinimo,
        @RequestParam(value = "precioFinalMaximo", required = false, defaultValue = "3200") double precioFinalMaximo)
    {
        List<Producto> productos = productoService.encontrarPorSubcategoriaCategoriaIdCategoriaYTipoDescuentoYFabricante_IdFabricanteYPrecioFinalEntre(idCategoria, tipoDescuento, idFabricante, precioFinalMinimo, precioFinalMaximo);
        if (productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productos);
    }
    
    //Busca por una palabra
    /*@GetMapping("/buscar")
    public ResponseEntity<List<Producto>> buscarProductosPorPalabraClave(@RequestParam(value = "palabra", required = false) String palabra) {
        List<Producto> productos = productoService.buscarPorPalabraClave(palabra);
        if (productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productos);
    }*/
    
    //Buscar por varias palabras
    @GetMapping("/buscar")
    public ResponseEntity<List<Producto>> buscarProductosPorPalabrasClave(
        @RequestParam(value = "palabras", required = false, defaultValue = "") String palabras) {
        
        List<Producto> productos = productoService.buscarPorPalabrasClave(palabras);
        if (productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productos);
    }
    
    @GetMapping("/buscar2")
    public ResponseEntity<List<Producto>> buscarProductosPorPalabrasClave2(
        @RequestParam(value = "palabras", required = false, defaultValue = "") String palabras) {
        
        List<Producto> productos = productoService.buscarPorPalabrasClave2(palabras);
        if (productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productos);
    }
    
    @GetMapping("/filtrarybuscar")
    public ResponseEntity<List<Producto>> getPorFiltrosYBusquedaPorPalabras(
            @RequestParam(value = "idCategoria", required = false) Integer idCategoria,
            @RequestParam(value = "tipoDescuento", required = false, defaultValue = "sin_descuento") TipoDescuento tipoDescuento,
            @RequestParam(value = "idFabricante", required = false) Integer idFabricante,
            @RequestParam(value = "precioFinalMinimo", required = false, defaultValue = "0") double precioFinalMinimo,
            @RequestParam(value = "precioFinalMaximo", required = false, defaultValue = "3200") double precioFinalMaximo,
            @RequestParam(value = "palabras", required = false) String palabras)
    {
        List<Producto> productos = productoService.filtrarYBuscarPorPalabras(
            idCategoria, tipoDescuento, idFabricante, precioFinalMinimo, precioFinalMaximo, palabras);
        if (productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productos);
    }
    
    @GetMapping("/filtrarybuscar2")
    public ResponseEntity<List<Producto>> getPorFiltrosYBusquedaPorPalabras2(
            @RequestParam(value = "idCategoria", required = false) Integer idCategoria,
            @RequestParam(value = "tipoDescuento", required = false, defaultValue = "sin_descuento") TipoDescuento tipoDescuento,
            @RequestParam(value = "idFabricante", required = false) Integer idFabricante,
            @RequestParam(value = "precioFinalMinimo", required = false, defaultValue = "0") double precioFinalMinimo,
            @RequestParam(value = "precioFinalMaximo", required = false, defaultValue = "3200") double precioFinalMaximo,
            @RequestParam(value = "palabras", required = false) String palabras)
    {
        List<Producto> productos = productoService.filtrarYBuscarPorPalabras2(
            idCategoria, tipoDescuento, idFabricante, precioFinalMinimo, precioFinalMaximo, palabras);
        if (productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productos);
    }
    
    /*@GetMapping("/filtro")
    public ResponseEntity<List<Producto>> getPorSubcategoria_IdSubcategoriaYFabricante_IdFabricanteYPrecioFinal(
            @RequestParam(value = "idSubcategoria", required = false) Integer idSubcategoria,
            @RequestParam(value = "idFabricante", required = false) Integer idFabricante,
            @RequestParam(value = "precioFinalMinimo", required = false, defaultValue = "0") double precioFinalMinimo,
            @RequestParam(value = "precioFinalMaximo", required = false, defaultValue = "3200") double precioFinalMaximo)
    {

        List<Producto> productos = productoService.encontrarPorSubcategoria_IdSubcategoriaYFabricante_IdFabricanteYPrecioFinalEntre(idSubcategoria, idFabricante, precioFinalMinimo, precioFinalMaximo);
        if (productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productos);
    }*/
        
    /* CREACIÓN------------------------------------------------------------------*/
	@PostMapping
	public ResponseEntity<?> postCrearYGuardar(@RequestBody ProductoDtoCreacion productoDtoCreacion) {
        Producto productoGuardado = productoService.crearYGuardar(productoDtoCreacion);
        return ResponseEntity.ok(productoGuardado);
	}

	/* ACTUALIZACIÓN--------------------------------------------------------------*/
    @PutMapping("/{idProducto}")
    public ResponseEntity<?> putActualizar(@PathVariable int idProducto, @RequestBody ProductoDtoCreacion productoDtoActualizacion) {
        try {
            Producto productoGuardado = productoService.actualizar(idProducto, productoDtoActualizacion);
            return ResponseEntity.ok(productoGuardado);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", ex.getMessage()));
        }
   }
    
    /* BORRADO-----------------------------------------------------------------------*/
    @DeleteMapping("/{idProducto}")
    public ResponseEntity<?> deletePorId(@PathVariable int idProducto) {
       try {
    	   productoService.borrarPorId(idProducto);
    	   return ResponseEntity.ok(Map.of("mensaje", "Producto borrado correctamente."));
       } catch (EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", ex.getMessage()));
	   } catch (IllegalStateException ex) {
		   return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("mensaje", ex.getMessage()));
       }
    }
}
