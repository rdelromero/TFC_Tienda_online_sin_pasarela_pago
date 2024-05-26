package com.nombreGrupo.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nombreGrupo.modelo.entities.LineaFacturacion;

@Repository
public interface LineaFacturacionRepository extends JpaRepository<LineaFacturacion, Integer>{
	
	boolean existsByProducto_IdProducto(int idProducto);
	List<LineaFacturacion> findByPedido_IdPedido(int identidadPedido);
	
	@Query("SELECT SUM(lf.cantidad) FROM LineaFacturacion lf WHERE lf.producto.idProducto = :idProducto AND lf.estado = 'activo'")
    Optional<Integer> encontrarUnidadesVendidasPorIdProducto(int idProducto);
}
