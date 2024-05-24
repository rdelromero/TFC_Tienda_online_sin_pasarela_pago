package com.nombreGrupo.seguridad;

import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Set;

@Service
public class JwtListaNegraService {
    private Set<String> listaNegra = new HashSet<>();

    public void blacklistToken(String token) {
        listaNegra.add(token);
    }

    public boolean isTokenBlacklisted(String token) {
        return listaNegra.contains(token);
    }
}