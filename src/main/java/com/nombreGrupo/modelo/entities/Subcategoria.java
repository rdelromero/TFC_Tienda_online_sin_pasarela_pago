package com.nombreGrupo.modelo.entities;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
@Table(name = "subcategorias")
@NamedQuery(name="Subcategoria.findAll", query="SELECT s FROM Subcategoria s")
public class Subcategoria implements Serializable{
	private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_subcategoria")
    private int idSubcategoria;

    @ManyToOne
    @JoinColumn(name="identidad_categoria")
    private Categoria categoria;
    
    @Column(nullable = false, length = 30)
    private String nombre;

    @Column(nullable = false)
    @JsonIgnore // Pueden ser párrafos así que mejor que no salga en un json por
    private String descripcion;

    @Column(name = "imagen_url", length = 100)
    private String imagenUrl;

}
