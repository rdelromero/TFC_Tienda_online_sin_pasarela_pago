package com.nombreGrupo.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nombreGrupo.modelo.entities.Pedido;
import com.nombreGrupo.modelo.entities.Pedido.EstadoPedido;
//import com.nombreGrupo.modelo.entities.Pedido.EstadoPedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
    
	List<Pedido> findByEstado(EstadoPedido estado);
    List<Pedido> findByCiudad(String ciudad);
    List<Pedido> findByPrecioTotalGreaterThanEqual(double precio);  // Método que usa convención de nombre
    
    //@Query("SELECT p FROM Pedido p WHERE p.usuario.idUsuario = :idUsuario")
    List<Pedido> findByUsuario_IdUsuario(int idUsuario); // Asumiendo que el campo en Usuario se llama idUsuario
    
    
  
}