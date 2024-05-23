package com.nombreGrupo.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nombreGrupo.modelo.dto.SubcategoriaDtoCreacion;
import com.nombreGrupo.modelo.entities.Producto;
import com.nombreGrupo.modelo.entities.Subcategoria;

public interface SubcategoriaService {

	//Lectura
	List<Subcategoria> encontrarTodas();
	Page<Subcategoria> encontrarTodasPaginacion(Pageable pageable);
	Subcategoria encontrarPorId(int idSubcategoria);
	Subcategoria encontrarPorNombre(String nombre);
	List<Producto> encontrarProductosPorSubcategoria_IdSubcategoria(int idSubcategoria);
	
	//CREACIÓN
	Subcategoria crearYGuardar(SubcategoriaDtoCreacion subcategoriadtocreacion);
	
	//ACTUALIZACIÓN
	Subcategoria actualizar(int idSubcategoria, SubcategoriaDtoCreacion subcategoriadtoactualizacion);
	
	//BORRAR
	boolean borrarPorId(int idSubcategoria);
	
}
