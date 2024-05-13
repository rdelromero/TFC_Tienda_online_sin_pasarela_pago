package com.nombreGrupo.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nombreGrupo.modelo.dto.EnvioDtoActualizacion;
import com.nombreGrupo.modelo.dto.EnvioDtoCreacion;
import com.nombreGrupo.modelo.entities.Envio;

public interface EnvioService {
	//Lectura
	List<Envio> encontrarTodos();
	Page<Envio> encontrarTodos(Pageable pageable);
	Envio encontrarPorId(int idEnvio);
	//Registro
	Envio crearYGuardar(EnvioDtoCreacion envioDtoCreacion);
	//Actualización
	Envio actualizar(int idEnvio, EnvioDtoActualizacion envioDtoActualizacion);
	//Borrado
	boolean borrarPorId(int idEnvio);
	
}
