package com.nombreGrupo.services;

import java.util.List;

import com.nombreGrupo.modelo.dto.CategoriaDtoCreacion;
import com.nombreGrupo.modelo.entities.Categoria;
import com.nombreGrupo.modelo.entities.Producto;

public interface CategoriaService {

	//LECTURA
	List<Categoria> encontrarTodas();
	Categoria encontrarPorId(int idCategoria);
	Categoria encontrarPorNombre(String nombre);
	List<Producto> encontrarProductosPorSubcategoriaCategoriaIdCategoria(int idCategoria);
	
	//CREACIÓN
	Categoria crearYGuardar(CategoriaDtoCreacion categoriadtocreacion);
	
	//ACTUALIZACIÓN
	Categoria actualizar(int idCategoria, CategoriaDtoCreacion categoriadtoactualizacion);
	
	//BORRADO
	boolean borrarPorId(int idCategoria);

}
