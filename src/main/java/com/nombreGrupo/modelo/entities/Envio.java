package com.nombreGrupo.modelo.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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
import jakarta.persistence.OneToOne;
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
@Table(name = "envios")
@NamedQuery(name="Envio.findAll", query="SELECT e FROM Envio e")
public class Envio implements Serializable {

	 private static final long serialVersionUID = 1L;
	
	 @Id
	 @GeneratedValue(strategy = GenerationType.IDENTITY)
	 @Column(name="id_envio")
	 private int idEnvio;

	 @ManyToOne
	 @JoinColumn(name = "identidad_pedido", nullable = false)
	 private Pedido pedido;
	 
	 @Column(name = "fecha_entrega")
	 private LocalDateTime fechaEntrega;
	 
	 @Column(name = "numero_documento_identidad_receptor", length = 20)
	 private String numeroDocumentoIdentidadReceptor;

	 @Column(length = 150)
	 private String comentario;

	 @Column(name = "fecha_entrega_vuelta_almacen")
	 private LocalDateTime fechaEntregaVueltaAlmacen;
	 
	//Campos ni insertables ni actualizables directamente ---------------------------*/
	 
	 @Column(name = "fecha_envio", nullable = false, updatable = false, insertable = false)
	 private LocalDateTime fechaEnvio;
	 
	 @Column(name = "fecha_actualizacion", nullable = false, updatable = false, insertable = false)
	 private LocalDateTime fechaActualizacion;
	
}
