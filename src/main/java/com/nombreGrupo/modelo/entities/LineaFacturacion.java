package com.nombreGrupo.modelo.entities;

import java.io.Serializable;

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
@Table(name = "linea_facturacion")
@NamedQuery(name="LineaFacturacion.findAll", query="SELECT l FROM LineaFacturacion l")
public class LineaFacturacion implements Serializable{

	private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_linea_facturacion")
    private int idLineaFacturacion;
    
    @ManyToOne
    @JoinColumn(name = "identidad_pedido", nullable = false)
    @JsonIgnore // Dado que en pedido quiero aparezcan las lineas de facturacion, no puede ser que en una línea de facturación aparezca también el pedido al que pertenece, por que habría recursión. de ahí que se ponga @JsonIgnote
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "identidad_producto", nullable = false)
    private Producto producto;
    
    @Column(nullable = false)
    private int cantidad;

    @Column(name = "precio_unitario", nullable = false)
    private double precioUnitario;

    @Column(name = "precio_linea", nullable = false)
    private double precioLinea;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Estado estado;
    
    public enum Estado {
        activo, cancelado;
    }
}
