package com.nombreGrupo.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nombreGrupo.modelo.entities.Subcategoria;
import com.nombreGrupo.repositories.SubcategoriaRepository;

@Service
public class SubcategoriaServiceImplMy8 implements SubcategoriaService{

	@Autowired
	private SubcategoriaRepository subcategoriaRepository;
	
	@Override
	public List<Subcategoria> encontrarTodas() {
		return subcategoriaRepository.findAll();
	}

}
