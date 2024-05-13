package com.nombreGrupo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nombreGrupo.modelo.entities.Resena;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Integer>{

	boolean existsByProducto_IdProducto(int idProducto);
	
}
