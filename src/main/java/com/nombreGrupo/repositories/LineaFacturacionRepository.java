package com.nombreGrupo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nombreGrupo.modelo.entities.LineaFacturacion;

@Repository
public interface LineaFacturacionRepository extends JpaRepository<LineaFacturacion, Integer>{
	
	boolean existsByProducto_IdProducto(int idProducto);
	
	List<LineaFacturacion> findByPedido_IdPedido(int identidadPedido);
}
