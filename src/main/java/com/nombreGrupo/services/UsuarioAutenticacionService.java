package com.nombreGrupo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nombreGrupo.modelo.dto.UsuarioDtoLogin;
import com.nombreGrupo.modelo.entities.Usuario;
import com.nombreGrupo.repositories.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class UsuarioAutenticacionService {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
    public String loguearse(UsuarioDtoLogin loginDto) {
        Usuario usuario = usuarioRepository.findByDireccionEmail(loginDto.getDireccionEmail())
            .orElseThrow(() -> new EntityNotFoundException("No hay ningún usuario con esa dirección de email."));
        if (!usuario.getActive()) {
            throw new IllegalStateException("La cuenta no está verificada.");
        }
        if (!loginDto.getPassword().equals(usuario.getPassword())) {
            throw new IllegalArgumentException("La contraseña es incorrecta.");
        }
        return "Logueo exitoso.";
    }
}
