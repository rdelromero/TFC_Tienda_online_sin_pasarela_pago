package com.nombreGrupo.modelo.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
@Table(name = "usuarios")
@NamedQuery(name="Usuario.findAll", query="SELECT u FROM Usuario u")
public class Usuario implements Serializable{
	private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_usuario")
    @JsonIgnoreProperties // Evita que salga las lineas_Facturacion. Lo hacemos porque de mostrarlo saldría un ciclo infinito
    private int idUsuario;

    @Column(name = "direccion_email", nullable = false, unique = true, length = 100)
    private String direccionEmail;
    
    @Column(nullable = false, length = 30)
    private String nombre;

    @Column(nullable = false, length = 40)
    private String apellido1;

    @Column(length = 40)
    private String apellido2;
    
    @Column(length = 30)
    private String password;

    //Campos ni insertables ni actualizables directamente ---------------------------*/
    
    @Column(nullable = false)
    private Boolean active = false;

    @Column(nullable = false, length = 6)
    private String otp;
    
    @Column(name = "fecha_generacion_otp")
    private LocalDateTime fechaGeneracionOtp;
    
    @Column(name = "fecha_creacion", nullable = false, updatable = false, insertable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @Column(name = "fecha_actualizacion", nullable = false, updatable = false, insertable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaActualizacion;

}