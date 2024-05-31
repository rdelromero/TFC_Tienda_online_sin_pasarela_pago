package com.nombreGrupo.services;

import java.util.List;

import com.nombreGrupo.modelo.dto.ResenaDtoCreacion;
import com.nombreGrupo.modelo.entities.Resena;

public interface ResenaService {
	//Lectura
	List<Resena> encontrarTodas();

	//Creación
	Resena crearYGuardar(ResenaDtoCreacion resenaDtoCreacion);
	
	//Borrado
	boolean borrarPorId(int idResena);

	
	
}
