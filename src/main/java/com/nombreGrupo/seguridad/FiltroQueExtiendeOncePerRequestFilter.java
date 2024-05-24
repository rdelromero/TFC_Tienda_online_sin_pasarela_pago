package com.nombreGrupo.seguridad;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class FiltroQueExtiendeOncePerRequestFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JwtListaNegraService jwtListaNegraService;
    
    // OncePerRequestFilter exige implementar doFilterInternal
    @Override
    protected void doFilterInternal(HttpServletRequest solicitud, HttpServletResponse respuesta, FilterChain cadena)
            throws ServletException, IOException {

        final String encabezadoAutorizacion = solicitud.getHeader("Authorization");

        String nombreUsuario = null;
        String jwt = null;

        if (encabezadoAutorizacion != null && encabezadoAutorizacion.startsWith("Bearer ")) {
            jwt = encabezadoAutorizacion.substring(7);
            nombreUsuario = jwtUtil.extraerUsername(jwt);
        }

        if (nombreUsuario != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            if (jwtListaNegraService.isTokenBlacklisted(jwt)) {
                respuesta.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        	
            UserDetails detallesUsuario = this.userDetailsServiceImpl.loadUserByUsername(nombreUsuario);

            if (jwtUtil.validarToken(jwt, detallesUsuario)) {

                UsernamePasswordAuthenticationToken tokenAutenticacion = new UsernamePasswordAuthenticationToken(
                        detallesUsuario, null, detallesUsuario.getAuthorities());
                tokenAutenticacion
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(solicitud));
                SecurityContextHolder.getContext().setAuthentication(tokenAutenticacion);
            }
        }
        cadena.doFilter(solicitud, respuesta);
    }
    
    
}