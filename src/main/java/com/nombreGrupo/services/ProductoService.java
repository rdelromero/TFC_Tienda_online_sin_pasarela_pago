package com.nombreGrupo.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nombreGrupo.modelo.dto.ProductoDtoCreacion;
import com.nombreGrupo.modelo.entities.Fabricante;
import com.nombreGrupo.modelo.entities.Imagen;
import com.nombreGrupo.modelo.entities.Pedido;
import com.nombreGrupo.modelo.entities.Producto;
import com.nombreGrupo.modelo.entities.Producto.TipoDescuento;

public interface ProductoService {

	//Lectura
	List<Producto> encontrarTodos();
	Page<Producto> encontrarTodos(Pageable pageable);
	Producto encontrarPorId(int idProducto);
	
	List<Producto> encontrarPorTipoDescuento(TipoDescuento tipoDescuento);
	List<Imagen> encontrarImagenesPorIdProducto(int productoId);
	
	//Búsqueda por filtros: idCategoria, tipoDescuento, idFabricante, precioFinalMinimo, precioFinalMaximo
	List<Producto> encontrarPorSubcategoriaCategoriaIdCategoriaYTipoDescuentoYFabricante_IdFabricanteYPrecioFinalEntre(Integer idCategoria, TipoDescuento tipoDescuento,
			Integer idFabricante, double precioFinalMinimo, double precioFinalMaximo);
	
	//Búsqueda por 1 palabra
	/*List<Producto> buscarPorPalabraClave(String palabra);*/
	
	//Búsqueda por varias palabras
	List<Producto> buscarPorPalabrasClave(String palabras);
	List<Producto> buscarPorPalabrasClave2(String palabras);
	
	//Búsqueda por filtro y varias palabras
	List<Producto> filtrarYBuscarPorPalabras(Integer idCategoria, TipoDescuento tipoDescuento,
			Integer idFabricante, double precioMinimo, double precioMaximo, String palabras);
	List<Producto> filtrarYBuscarPorPalabras2(Integer idCategoria, TipoDescuento tipoDescuento, Integer idFabricante,
			double precioFinalMinimo, double precioFinalMaximo, String palabras);
	
	//Búsqueda por filtros: idSubcategoria, idFabricante, precioFinalMinimo, precioFinalMaximo
	/*List<Producto> encontrarPorSubcategoria_IdSubcategoriaYFabricante_IdFabricanteYPrecioFinalEntre(Integer idSubcategoria,
			Integer idFabricante, double precioFinalMinimo, double precioFinalMaximo);*/
	
	//Creación
	Producto crearYGuardar(ProductoDtoCreacion productoDtoCreacion);
	//Actualización
	Producto actualizar(int idProducto, ProductoDtoCreacion productoDtoActualizacion);
	Producto actualizarMVC(Producto producto);
	//Borrado
	boolean borrarPorId(int idProducto);
	
	
	

	
	
	
	
	
}
