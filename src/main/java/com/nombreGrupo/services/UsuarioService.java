package com.nombreGrupo.services;

import java.util.List;

import com.nombreGrupo.modelo.dto.UsuarioDtoRegistro;
import com.nombreGrupo.modelo.entities.Usuario;

public interface UsuarioService {

	//Lectura
	List<Usuario> encontrarTodos();
	List<Usuario> encontrarPorActiveTrue();
	Usuario encontrarPorId(int idUsuario);
	Usuario encontrarPorDireccionEmail(String direccionEmail);
	//Registro
	Usuario crearYGuardar(UsuarioDtoRegistro usuarioDtoRegistro);
	Usuario verificarCuentaPorDireccionEmailYOpt(String direccionEmail, String otp);
	Usuario regenerarOtpParaUsuarioNoVerificado(int idUsuario);
	//Actualización
	Usuario actualizar(int idUsuario, UsuarioDtoRegistro usuarioDtoRegistro);
	//Borrado
	boolean borrarPorId(int id);
	
	
	
	
	
	
	
	

	

}
