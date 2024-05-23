package com.nombreGrupo.modelo.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nombreGrupo.modelo.entities.Pedido.EstadoPedido;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
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
@Table(name = "fabricantes")
@NamedQuery(name="Fabricante.findAll", query="SELECT f FROM Fabricante f")
public class Fabricante implements Serializable{

	private static final long serialVersionUID = 1L;
		
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_fabricante")
    private int idFabricante;

    @Column(nullable = false, length = 30)
    private String nombre;

    @Column(name = "fecha_fundacion")
    private LocalDateTime fechaFundacion;
    
    @Column(length = 50)
    private String pais;
    
    @Column(name = "pagina_web", length = 50)
    private String paginaWeb;
    
    @Column(nullable = false)
    private String descripcion;

    @Column(name = "imagen_url", nullable = false, length = 100)
    private String imagenUrl;
	
    @Column(name = "fecha_creacion", nullable = false, updatable = false, insertable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion", nullable = false, updatable = false, insertable = false)
    private LocalDateTime fechaActualizacion;
    
    @JsonIgnore
    @OneToMany(mappedBy = "fabricante")
    private List<Producto> productos;
}
