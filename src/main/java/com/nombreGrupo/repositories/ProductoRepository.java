package com.nombreGrupo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nombreGrupo.modelo.entities.Producto;
import com.nombreGrupo.modelo.entities.Producto.TipoDescuento;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer>, JpaSpecificationExecutor<Producto>{
	
	boolean existsByFabricante_IdFabricante(int idFabricante);
	
	//@Query("SELECT p FROM Producto p WHERE p.subcategoria.idSubcategoria = :idSubcategoria")
	List<Producto> findBySubcategoria_IdSubcategoria(int idSubcategoria);
	List<Producto> findByFabricante_IdFabricante(int idFabricante);
	List<Producto> findByPrecioFinalGreaterThanEqual(double precioFinalMinimo);
	List<Producto> findByTipoDescuento(TipoDescuento tipoDescuento);
	
	//@Query("SELECT p FROM Producto p WHERE p.subcategoria.categoria.idCategoria = :idCategoria")
	List<Producto> findBySubcategoriaCategoriaIdCategoria(int idCategoria);
	
	List<Producto> findByTipoDescuentoAndPrecioFinalBetween(TipoDescuento tipoDescuento, double precioFinalMin, double precioFinalMax);
	List<Producto> findBySubcategoriaCategoriaIdCategoriaAndTipoDescuentoAndPrecioFinalBetween(int idCategoria, TipoDescuento tipoDescuento, double precioFinalMin, double precioFinalMax);
	List<Producto> findByTipoDescuentoAndFabricante_IdFabricanteAndPrecioFinalBetween(TipoDescuento tipoDescuento, int idFabricante, double precioFinalMin, double precioFinalMax);
	
	List<Producto> findBySubcategoriaCategoriaIdCategoriaAndTipoDescuentoAndFabricante_IdFabricanteAndPrecioFinalBetween(int idSubcategoria, TipoDescuento tipoDescuento, Integer idFabricante, double precioFinalMin, double precioFinalMax);

	//List<Producto> findBySubcategoria_IdSubcategoriaAndPrecioFinalBetween(int idSubcategoria, double precioFinalMin, double precioFinalMax);
	//List<Producto> findBySubcategoria_IdSubcategoriaAndFabricante_IdFabricanteAndPrecioFinalBetween(int idSubcategoria, Integer idFabricante, double precioFinalMin, double precioFinalMax);
	
	
	
}

