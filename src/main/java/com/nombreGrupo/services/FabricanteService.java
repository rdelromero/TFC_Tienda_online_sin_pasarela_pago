package com.nombreGrupo.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nombreGrupo.modelo.dto.FabricanteDtoCreacion;
import com.nombreGrupo.modelo.entities.Fabricante;
import com.nombreGrupo.modelo.entities.Producto;

public interface FabricanteService {

	//Métodos get
	List<Fabricante> encontrarTodos();
	Page<Fabricante> encontrarTodosPaginacion(Pageable pageable);
	Fabricante encontrarPorId(int idFabricante);
	//El parámetro nombre puede estar escrito en forma normal (dejando espacios) o en kebab-case
	Fabricante encontrarPorNombre(String nombre);
	List<Producto> encontrarProductosPorFabricante_IdFabricante(int idFabricante);
	
	//Métodos post
	Fabricante crearYGuardar(FabricanteDtoCreacion fabricanteDtoCreacion);
	
	//Métodos put
	Fabricante actualizar(int idFabricante, FabricanteDtoCreacion fabricanteDtoCreacion);
	
	//Métodos delete
	boolean borrarPorId(int id);
	void eliminarFabricanteConProductos(int idFabricante);
	
	
	

	



	

}
