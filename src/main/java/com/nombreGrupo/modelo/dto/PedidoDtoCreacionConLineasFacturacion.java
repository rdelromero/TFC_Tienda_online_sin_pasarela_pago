package com.nombreGrupo.modelo.dto;

import java.util.List;

import com.nombreGrupo.modelo.entities.Pedido.MetodoEnvio;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PedidoDtoCreacionConLineasFacturacion {
	private int idPedido;
	private int idUsuario;
    private String nombre;
    private String apellidos;
    private String direccion;
    private String pais;
    private String ciudad;
    private String numeroTelefonoMovil;
    private MetodoEnvio metodoEnvio;
    private List<LineaFacturacionDto> lineasFacturacionDto;
}
