package com.nombreGrupo.seguridad;

import com.nombreGrupo.modelo.dto.UsuarioDtoLogin;
import com.nombreGrupo.modelo.entities.Usuario;
import com.nombreGrupo.repositories.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UsuarioAutenticacionService {


    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    
    @Autowired
    private JwtUtil jwtUtil;

    public String autenticarYRetornarJWT(UsuarioDtoLogin usuarioDtoLogin) throws Exception {
        // Verificar si el usuario está activo
    	
        Usuario usuario = usuarioRepository.findByUsername(usuarioDtoLogin.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("No existe un usuario de username " + usuarioDtoLogin.getUsername() + "."));

        if (!usuario.getActive()) {
        	throw new IllegalStateException("La cuenta no está verificada.");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(usuarioDtoLogin.getUsername(), usuarioDtoLogin.getPassword())
            );
        } catch (Exception e) {
            throw new Exception("Usuario o contraseña incorrectos", e);
        }

        final UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(usuarioDtoLogin.getUsername());
        return jwtUtil.crearJWTConMapaVacioDeClaims(userDetails);
    }
    
}