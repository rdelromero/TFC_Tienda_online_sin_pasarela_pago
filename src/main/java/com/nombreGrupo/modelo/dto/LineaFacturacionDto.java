package com.nombreGrupo.modelo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LineaFacturacionDto {

	private int idLineaFacturacion;
    private int idProducto;
    private int cantidad;
}