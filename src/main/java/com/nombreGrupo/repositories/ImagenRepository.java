package com.nombreGrupo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.nombreGrupo.modelo.entities.Imagen;

@Repository
public interface ImagenRepository extends JpaRepository<Imagen, Integer> {
	
	List<Imagen> findByProducto_IdProducto(int idProducto);
}
