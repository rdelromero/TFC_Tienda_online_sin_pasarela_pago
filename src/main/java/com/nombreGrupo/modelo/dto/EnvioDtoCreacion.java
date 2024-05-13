package com.nombreGrupo.modelo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnvioDtoCreacion {
	
	private int idEnvio;
	private int idPedido;
	private String comentario;
}
