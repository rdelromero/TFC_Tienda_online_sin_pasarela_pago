package com.nombreGrupo.modelo.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnvioDtoActualizacion implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int idEnvio;
	private LocalDateTime fechaEntrega;
	private String numeroDocumentoIdentidadReceptor;
	private LocalDateTime fechaEntregaVueltaAlmacen;
	private String comentario;
	
}
