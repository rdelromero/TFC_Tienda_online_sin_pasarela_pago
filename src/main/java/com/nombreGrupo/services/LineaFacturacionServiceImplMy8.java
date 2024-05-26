package com.nombreGrupo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nombreGrupo.repositories.LineaFacturacionRepository;

@Service
public class LineaFacturacionServiceImplMy8 implements LineaFacturacionService {

	@Autowired
	private LineaFacturacionRepository lineaFacturacionRepository;

	@Override
	public int encontrarNumeroUnidadesVendidasPorIdProducto(int idProducto) {
	    return lineaFacturacionRepository.encontrarUnidadesVendidasPorIdProducto(idProducto).orElse(0);
	}
}


