package com.nombreGrupo.services;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.nombreGrupo.modelo.dto.UsuarioDtoRegistro;
import com.nombreGrupo.modelo.entities.Pedido;
import com.nombreGrupo.modelo.entities.Usuario;

public interface UsuarioService {

	//Lectura
	List<Usuario> encontrarTodos();
	Page<Usuario> encontrarTodosPaginacion(Pageable pageable);
	List<Usuario> encontrarPorActiveTrue();
	Usuario encontrarPorId(int idUsuario);
	Usuario encontrarPorUsername(String direccionEmail);
	List<Pedido> encontrarPedidosPorUsuario_IdUsuario(int idUsuario);
	
	//Registro
	Usuario crearYGuardar(UsuarioDtoRegistro usuarioDtoRegistro);
	String verificarCuentaPorDireccionEmailYUuid(String uuid);
	Usuario regenerarUuidParaUsuarioNoVerificado(int idUsuario);
	Usuario crearAdmin(UsuarioDtoRegistro usuarioDto);
	
	//Actualización
	Usuario actualizar(int idUsuario, UsuarioDtoRegistro usuarioDtoRegistro);
	
	//Borrado
	boolean borrarPorId(int id);
	
	
	
}
