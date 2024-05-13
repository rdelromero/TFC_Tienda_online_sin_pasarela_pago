package com.nombreGrupo.services;

import java.util.List;

import com.nombreGrupo.modelo.entities.Resena;

public interface ResenaService {
	//Lectura
	List<Resena> encontrarTodas();
	//Borrado
	boolean borrarPorId(int idResena);
}
