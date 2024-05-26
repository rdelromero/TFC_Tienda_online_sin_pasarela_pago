package com.nombreGrupo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.nombreGrupo.modelo.entities.Resena;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Integer>{

	boolean existsByProducto_IdProducto(int idProducto);
	List<Resena> findByProducto_IdProducto(int idProducto);
	
	// SIN USAR PORQUE HEMOS DEFINIDO UN CAMPO numeroValoraciones EN LA ENTIDAD
	/*@Query("SELECT COUNT(r) FROM Resena r WHERE r.producto.idProducto = :idProducto")
	int contarResenasPorIdProducto(int idProducto);*/
	


}
