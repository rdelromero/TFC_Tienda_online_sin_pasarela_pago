package com.nombreGrupo.modelo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResenaDtoCreacion {
	
	private int idResena;
	private int idProducto;
	private int idUsuario;
	private int valoracion;
	private String titulo;
	private String comentario;
	
}
