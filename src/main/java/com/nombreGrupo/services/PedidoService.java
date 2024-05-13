package com.nombreGrupo.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nombreGrupo.modelo.dto.PedidoDtoActualizacionSinCambiarLineasFacturacion;

import com.nombreGrupo.modelo.entities.Pedido;
import com.nombreGrupo.modelo.entities.Pedido.EstadoPedido;
import com.nombreGrupo.modelo.entities.Pedido.MetodoEnvio;


public interface PedidoService {
	//Lectura
	List<Pedido> encontrarTodos();
	Page<Pedido> encontrarTodos(Pageable pageable);
	Pedido encontrarPorId(int idPedido);
	List<Pedido> encontrarPorPais(String ciudad);
	List<Pedido> encontrarPorPrecioTotalMayorOIgualQue(double precio);
	List<Pedido> encontrarPorEstado(EstadoPedido estado); //estado puede ser pendiente, enviado, entregado, cancelado
	
	//Creacion
	Pedido crearYGuardarConLF(int idUsuario, List<Integer> productoIds, List<Integer> cantidades, String descripcion, String apellidos,
			String direccion, String pais, String ciudad, String numeroTelefonoMovil, MetodoEnvio metodoEnvio);
	
	//Actualización
	Pedido actualizarSinCambiarLF(int idPedido, PedidoDtoActualizacionSinCambiarLineasFacturacion pedidoDtoActualizacion);
	
	//Borrado
	boolean borrarPorId(int id);
	List<Pedido> encontrarPorUsuarioIdUsuario(int idUsuario);
	
	
}
