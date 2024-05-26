package com.nombreGrupo.services;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nombreGrupo.especificaciones.ProductoEspecificaciones;
import com.nombreGrupo.modelo.dto.LineaFacturacionDto;
import com.nombreGrupo.modelo.dto.ImagenDto;
import com.nombreGrupo.modelo.dto.ProductoDtoCreacion;
import com.nombreGrupo.modelo.entities.Fabricante;
import com.nombreGrupo.modelo.entities.Imagen;
import com.nombreGrupo.modelo.entities.LineaFacturacion;
import com.nombreGrupo.modelo.entities.Producto;
import com.nombreGrupo.modelo.entities.Producto.TipoDescuento;
import com.nombreGrupo.modelo.entities.Resena;
import com.nombreGrupo.modelo.entities.Subcategoria;
import com.nombreGrupo.repositories.ImagenRepository;
import com.nombreGrupo.repositories.LineaFacturacionRepository;
import com.nombreGrupo.repositories.FabricanteRepository;
import com.nombreGrupo.repositories.SubcategoriaRepository;
import com.nombreGrupo.repositories.ProductoRepository;
import com.nombreGrupo.repositories.ResenaRepository;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.modelmapper.ModelMapper;

@Service
public class ProductoServiceImplMy8 implements ProductoService{

	@Autowired
	private ProductoRepository productoRepository;
	
	@Autowired
	private FabricanteRepository fabricanteRepository;
	
	@Autowired
	private SubcategoriaRepository subcategoriaRepository;
	
	@Autowired
	private ImagenRepository imagenRepository;
	
	@Autowired
	private ResenaRepository resenaRepository;
	
	@Autowired
	private ModelMapper modeloMapper;

	@Autowired
	private LineaFacturacionRepository lineaFacturacionRepository;
	
	@Override
	public List<Producto> encontrarTodos() {
		return productoRepository.findAll();
	}

	@Override
	public Page<Producto> encontrarTodos(Pageable pageable) {
        return productoRepository.findAll(pageable);
    }
	
    @Override
    public Producto encontrarPorId(int idProducto) {
        return productoRepository.findById(idProducto)
                .orElseThrow(() -> new EntityNotFoundException("No existe un producto de idProducto "+idProducto+"."));
    }
	
    @Override
    public List<Producto> encontrarPorTipoDescuento(TipoDescuento tipoDescuento) {
        return productoRepository.findByTipoDescuento(tipoDescuento);
    }
	
    @Override
    public List<Imagen> encontrarImagenesPorIdProducto(int productoId) {
        return imagenRepository.findByProducto_IdProducto(productoId);
    }
    
    @Override
    public List<Resena> encontrarResenasPorIdProducto(int productoId) {
        return resenaRepository.findByProducto_IdProducto(productoId);
    }
    
	@Override
    public List<Producto> encontrarPorSubcategoriaCategoriaIdCategoriaYTipoDescuentoYFabricante_IdFabricanteYPrecioFinalEntre(Integer idCategoria, TipoDescuento tipoDescuento, Integer idFabricante, double precioFinalMinimo, double precioFinalMaximo) {
		if (idCategoria == null && idFabricante == null) {
	        // Ambos son nulos
	        return productoRepository.findByTipoDescuentoAndPrecioFinalBetween(tipoDescuento, precioFinalMinimo, precioFinalMaximo);
	    } else if (idFabricante == null) {
	        // Solo fabricante es nulo
	        return productoRepository.findBySubcategoriaCategoriaIdCategoriaAndTipoDescuentoAndPrecioFinalBetween(idCategoria, tipoDescuento, precioFinalMinimo, precioFinalMaximo);
	    } else if (idCategoria == null) {
			// Solo subcategoria es nulo
	        return productoRepository.findByTipoDescuentoAndFabricante_IdFabricanteAndPrecioFinalBetween(tipoDescuento, idFabricante, precioFinalMinimo, precioFinalMaximo);
	    } else {
	    	// Ninguno es nulo
	    	return productoRepository.findBySubcategoriaCategoriaIdCategoriaAndTipoDescuentoAndFabricante_IdFabricanteAndPrecioFinalBetween(idCategoria, tipoDescuento, idFabricante, precioFinalMinimo, precioFinalMaximo);
	    }
    }
	
	/*@Override
    public List<Producto> encontrarPorSubcategoria_IdSubcategoriaYFabricante_IdFabricanteYPrecioFinalEntre(Integer idSubcategoria, Integer idFabricante, double precioFinalMinimo, double precioFinalMaximo) {
		if (idSubcategoria == null && idFabricante == null) {
	        // Ambos son nulos
	        return productoRepository.findByPrecioFinalBetween(precioFinalMinimo, precioFinalMaximo);
	    } else if (idFabricante == null) {
	        // Solo fabricante es nulo
	        return productoRepository.findBySubcategoria_IdSubcategoriaAndPrecioFinalBetween(idSubcategoria, precioFinalMinimo, precioFinalMaximo);
	    } else if (idSubcategoria == null) {
			// Solo subcategoria es nulo
	        return productoRepository.findByFabricante_IdFabricanteAndPrecioFinalBetween(idFabricante, precioFinalMinimo, precioFinalMaximo);
	    } else {
	    	// Ninguno es nulo
	    	return productoRepository.findBySubcategoria_IdSubcategoriaAndFabricante_IdFabricanteAndPrecioFinalBetween(idSubcategoria, idFabricante, precioFinalMinimo, precioFinalMaximo);
	    }
    }*/
	
	//Búsqueda por 1 palabra
	/*@Override
    public List<Producto> buscarPorPalabraClave(String palabra) {
        Specification<Producto> spec = ProductoEspecificaciones.tienePalabraClave(palabra);
        return productoRepository.findAll(spec);
    }*/
	
	@Override
	public List<Producto> buscarPorPalabrasClave(String palabras) {
	    Specification<Producto> spec = ProductoEspecificaciones.contienePalabrasClave(palabras);
	    return productoRepository.findAll(spec);
	}
	
	//Búsqueda para que los primeros resultados sean los que más coincidencias tienen
	@Override
	public List<Producto> buscarPorPalabrasClave2(String palabras) {
        if (palabras == null || palabras.trim().isEmpty()) {
            return Collections.emptyList();
        }

        // Obtener los productos que coinciden con alguna de las palabras clave
        Specification<Producto> spec = ProductoEspecificaciones.contienePalabrasClave(palabras);
        List<Producto> productos = productoRepository.findAll(spec);

        // Dividir las palabras clave y convertirlas a minúsculas para comparaciones consistentes
        Set<String> palabrasClave = Arrays.stream(palabras.toLowerCase().split("\\s+"))
                                          .collect(Collectors.toSet());

        // Contar coincidencias y crear un mapa de Producto a conteo de coincidencias
        Map<Producto, Long> conteoDeCoincidencias = productos.stream()
            .collect(Collectors.toMap(
                producto -> producto,
                producto -> contarCoincidencias(producto, palabrasClave)
            ));

        // Ordenar productos por el número de coincidencias, de mayor a menor
        return productos.stream()
            .sorted(Comparator.comparingLong(producto -> conteoDeCoincidencias.get(producto)).reversed())
            .collect(Collectors.toList());
    }

    private long contarCoincidencias(Producto producto, Set<String> palabrasClave) {
        long conteo = 0;
        for (String palabra : palabrasClave) {
            if (producto.getNombre().toLowerCase().contains(palabra)) {
                conteo++;
            }
            if (producto.getFabricante().getNombre().toLowerCase().contains(palabra)) {
                conteo++;
            }
        }
        return conteo;
    }
	
    //Filtrado y búsqueda
    @Override
    public List<Producto> filtrarYBuscarPorPalabras(Integer idCategoria, TipoDescuento tipoDescuento, Integer idFabricante, double precioMinimo, double precioMaximo, String palabras) {
        Specification<Producto> spec = ProductoEspecificaciones.buscaYFiltra(
            idCategoria, tipoDescuento, idFabricante, precioMinimo, precioMaximo, palabras);
        return productoRepository.findAll(spec);
    }
    
    @Override
    public List<Producto> filtrarYBuscarPorPalabras2(Integer idCategoria, TipoDescuento tipoDescuento, Integer idFabricante,
            double precioFinalMinimo, double precioFinalMaximo, String palabras) {
        Specification<Producto> spec = ProductoEspecificaciones.buscaYFiltra(
            idCategoria, tipoDescuento, idFabricante, precioFinalMinimo, precioFinalMaximo, palabras);
        List<Producto> productos = productoRepository.findAll(spec);

        if (palabras != null && !palabras.isEmpty()) {
            Map<Producto, Integer> countMap = new HashMap<>();
            for (Producto producto : productos) {
                int count = 0;
                for (String palabra : palabras.toLowerCase().split("\\s+")) {
                    if (producto.getNombre().toLowerCase().contains(palabra) || producto.getDescripcion().toLowerCase().contains(palabra)) {
                        count++;
                    }
                }
                countMap.put(producto, count);
            }
            return productos.stream()
                .sorted((p1, p2) -> countMap.get(p2).compareTo(countMap.get(p1)))
                .collect(Collectors.toList());
        }
        return productos;
    }
        
    //Creación
	@Override
	public Producto crearYGuardar(ProductoDtoCreacion productoDtoCreacion) {
	    
		fabricanteRepository.findById(productoDtoCreacion.getIdFabricante())
                .orElseThrow(() -> new EntityNotFoundException("No existe fabricante con IdFabricante: " + productoDtoCreacion.getIdFabricante() + "."));

        subcategoriaRepository.findById(productoDtoCreacion.getIdSubcategoria())
                .orElseThrow(() -> new EntityNotFoundException("No existe subcategoria con IdSubcategoria: " + productoDtoCreacion.getIdSubcategoria() + "."));
		
        Producto producto = new Producto();
        modeloMapper.map(productoDtoCreacion, producto);
        producto = productoRepository.save(producto);
        
        for (ImagenDto imagenDto : productoDtoCreacion.getImagenesDto()) {
            Imagen imagen = new Imagen();  
            modeloMapper.map(imagenDto, imagen);
            imagen.setProducto(producto);
            imagenRepository.save(imagen);
        }
		
	    return producto;
	}
	
	@Override
	public Producto actualizar(int idProducto, ProductoDtoCreacion productoDtoActualizacion) {
		productoRepository.findById(idProducto)
    	        .orElseThrow(() -> new EntityNotFoundException("No existe producto de idProducto "+idProducto+"."));
		
		Producto producto = new Producto();
		modeloMapper.map(productoDtoActualizacion, producto);
		producto.setIdProducto(idProducto);
		
		if (producto.getTipoDescuento().toString().equals("porcentual")) {
			producto.setPrecioFinal(producto.getPrecio()-producto.getDescuento()/100*producto.getPrecio());
		} else if (producto.getTipoDescuento().toString().equals("absoluto")) {
			producto.setPrecioFinal(producto.getPrecio()-producto.getDescuento());
		} else {
			producto.setPrecioFinal(producto.getPrecio());
		}
		System.out.println(producto);
		return productoRepository.save(producto);
	}
	
	@Override
	public Producto actualizarMVC(Producto producto) {
		return productoRepository.save(producto);
	}
	
	@Override
	public boolean borrarPorId(int idProducto) {
	    // Verificar si el producto existe
	    productoRepository.findById(idProducto)
	            .orElseThrow(() -> new EntityNotFoundException("No existe producto con ID: " + idProducto + "."));

	    // Consolidar la verificación de existencia de relaciones
	    boolean tieneLineasDeFacturacion = lineaFacturacionRepository.existsByProducto_IdProducto(idProducto);
	    boolean tieneResenas = resenaRepository.existsByProducto_IdProducto(idProducto);

	    if (tieneLineasDeFacturacion && tieneResenas) {
	        throw new IllegalStateException("El producto NO ha sido borrado. Hay líneas de facturación y reseñas asociadas con este producto.");
	    }
	    if (tieneLineasDeFacturacion) {
	        throw new IllegalStateException("El producto NO ha sido borrado. Hay líneas de facturación asociadas con este producto.");
	    }
	    if (tieneResenas) {
	        throw new IllegalStateException("El producto NO ha sido borrado. Hay reseñas asociadas con este producto.");
	    }

	    // Si no hay dependencias, proceder con la eliminación
	    productoRepository.deleteById(idProducto);
	    return true;
	}

}
