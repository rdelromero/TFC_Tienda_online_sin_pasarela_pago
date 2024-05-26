package com.nombreGrupo.modelo.entities;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "resenas")
@NamedQuery(name="Resena.findAll", query="SELECT r FROM Resena r")
public class Resena implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_resena")
    private int idResena;

    @ManyToOne
    @JoinColumn(name="identidad_producto")
    private Producto producto;
    
    @ManyToOne
    @JoinColumn(name="identidad_usuario")
    private Usuario usuario;
    
    @Column(nullable = false)
    private int valoracion;
    
    @Column(nullable = false, length = 100)
    private String titulo;
    
    @Column(nullable = false, length = 1000)
    private String comentario;

  //Campos ni insertables ni actualizables directamente ---------------------------*/
    
    @Column(name = "fecha_creacion", nullable = false, updatable = false, insertable = false)
    private LocalDateTime fechaCreacion;
}
