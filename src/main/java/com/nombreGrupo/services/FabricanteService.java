package com.nombreGrupo.services;

import java.util.List;

import com.nombreGrupo.modelo.dto.FabricanteDtoCreacion;
import com.nombreGrupo.modelo.entities.Fabricante;

public interface FabricanteService {

	//Métodos get
	List<Fabricante> encontrarTodos();

	//Métodos post
	Fabricante crearYGuardar(FabricanteDtoCreacion fabricanteDtoCreacion);
	
	//Métodos put
	Fabricante actualizar(int idFabricante, FabricanteDtoCreacion fabricanteDtoCreacion);
	
	//Métodos delete
	boolean borrarPorId(int id);



	

}
