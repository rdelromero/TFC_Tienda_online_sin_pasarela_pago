package com.nombreGrupo.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nombreGrupo.modelo.entities.Fabricante;


@Repository
public interface FabricanteRepository extends JpaRepository<Fabricante, Integer>{
	
}
