package com.nombreGrupo.seguridad;

import com.nombreGrupo.modelo.entities.Usuario;
import com.nombreGrupo.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /* Esta función viene a ser lo mismo que return usuarioRepository.findByUsername(nombreUsuario) solo que
    ahora devuelve excepción UsernameNotFoundException, que es una excepción estándar en Spring Security*/
    @Override
    public UserDetails loadUserByUsername(String nombreUsuario) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(nombreUsuario)
        		.orElseThrow(() -> new UsernameNotFoundException("Usuario de username "+nombreUsuario+" no encontrado."));
        return usuario;
    }
}