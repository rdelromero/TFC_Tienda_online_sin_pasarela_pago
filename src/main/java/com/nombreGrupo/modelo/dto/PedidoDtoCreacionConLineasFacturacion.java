package com.nombreGrupo.modelo.dto;

import java.util.List;

import com.nombreGrupo.modelo.entities.Pedido.EstadoPedido;
import com.nombreGrupo.modelo.entities.Pedido.MetodoEnvio;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PedidoDtoCreacionConLineasFacturacion {
	
	private int idUsuario;
    private List<Integer> productoIds;
    private List<Integer> cantidades;
    private String nombre;
    private String apellidos;
    private String direccion;
    private String pais;
    private String ciudad;
    private String numeroTelefonoMovil;
    private MetodoEnvio metodoEnvio;
}
