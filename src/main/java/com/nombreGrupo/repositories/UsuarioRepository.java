package com.nombreGrupo.repositories;

import com.nombreGrupo.modelo.entities.Usuario;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
	Optional<Usuario> findByDireccionEmail(String email);
	List<Usuario> findByActiveTrue();
}