package com.nombreGrupo.modelo.dto;

import java.util.List;
import com.nombreGrupo.modelo.entities.Producto.TipoDescuento;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductoDtoCreacion {
	private int idProducto;
	private int idFabricante;
	private int idSubcategoria;
	private String nombre;
	private String descripcion;
	private String detalles;
	private double precio;
	private int stock;
	private boolean novedad;
	private TipoDescuento tipoDescuento;
	private double descuento;
	//FUTURA IMPLEMENTACIÓN
	private List<ImagenDto> imagenesDto;
}
