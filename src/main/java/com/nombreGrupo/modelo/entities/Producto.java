package com.nombreGrupo.modelo.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "productos")
@NamedQuery(name="Producto.findAll", query="SELECT p FROM Producto p")

public class Producto implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_producto")
    private int idProducto;

    @ManyToOne
    @JoinColumn(name="identidad_fabricante")
    private Fabricante fabricante;
    
    @ManyToOne
    @JoinColumn(name="identidad_subcategoria")
    private Subcategoria subcategoria;

    //IMPORTANTE Con @JSonIgnore no saldré este campo en el json. Así evitamos mostrar el ciclo infinito que resulta al hacer petición GET para mostrar un pedido.
    @JsonIgnore // 
    @OneToMany(mappedBy = "producto")
    private List<LineaFacturacion> lineasFacturacion;
    
    @Column(nullable = false, length = 30)
    private String nombre;

    @Column(nullable = false)
    private String descripcion;

    @Column(length = 30)
    private String detalles;
    
    @Column(nullable = false, length = 80)
    private double precio;
    
    @Column(length = 40)
    private int stock;

    @Column
    private boolean novedad;
    
    @Enumerated(EnumType.STRING)
    @Column(name="tipo_descuento", length = 10)
    private TipoDescuento tipoDescuento;  // EstadoPedido es un enum.

    @Column
    private Double descuento;
    
    //Campos ni insertables ni actualizables directamente ---------------------------*/
    
    @Column(name="precio_final", insertable = false)
    private double precioFinal = precio;
    
    @Column(name="numero_valoraciones", nullable = false)
    private int numeroValoraciones = 0;
    
    @Column(name="valoracion_media", insertable = false)
    private Double valoracionMedia;

    @Column(name = "fecha_creacion", nullable = false, updatable = false, insertable = false)
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_actualizacion", nullable = false, updatable = false, insertable = false)
    private LocalDateTime fechaActualizacion;

    @JsonIgnore
    @OneToMany(mappedBy = "producto")
    private List<Imagen> imagenes;
    
    public enum TipoDescuento {
        sin_descuento, porcentual, absoluto;
    }
}
