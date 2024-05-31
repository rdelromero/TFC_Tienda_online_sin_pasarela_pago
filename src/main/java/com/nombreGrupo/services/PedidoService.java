package com.nombreGrupo.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nombreGrupo.modelo.dto.PedidoDtoActualizacionSinCambiarLineasFacturacion;
import com.nombreGrupo.modelo.dto.PedidoDtoCreacionConLineasFacturacion;
import com.nombreGrupo.modelo.entities.Pedido;
import com.nombreGrupo.modelo.entities.Pedido.EstadoPedido;

public interface PedidoService {
	//Lectura
	List<Pedido> encontrarTodos();
	Page<Pedido> encontrarTodos(Pageable pageable);
	Pedido encontrarPorId(int idPedido);
	List<Pedido> encontrarPorPais(String ciudad);
	List<Pedido> encontrarPorPrecioTotalMayorOIgualQue(double precio);
	List<Pedido> encontrarPorEstado(EstadoPedido estado); //estado puede ser pendiente, enviado, entregado, cancelado
	List<Pedido> encontrarPorUsuario_IdUsuario(int idUsuario);
	
	//Creacion
	Pedido crearYGuardarConLF(PedidoDtoCreacionConLineasFacturacion pedidoDtoCreacionConLF);
	
	//Actualización por admin
	Pedido actualizarSinCambiarLF(int idPedido, PedidoDtoActualizacionSinCambiarLineasFacturacion pedidoDtoActualizacion);
	//Actualización por el propio comprador
	Pedido actualizarSinCambiarLFUsuario(int idUsuarioDelJwt, int idPedido,
			PedidoDtoActualizacionSinCambiarLineasFacturacion pedidoDtoActualizacion);
	
	//Borrado
	boolean borrarPorId(int id);
	
}
