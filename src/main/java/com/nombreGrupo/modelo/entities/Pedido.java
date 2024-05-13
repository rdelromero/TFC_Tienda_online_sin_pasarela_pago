package com.nombreGrupo.modelo.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "pedidos")
@NamedQuery(name="Pedido.findAll", query="SELECT p FROM Pedido p")
public class Pedido implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_pedido")
    private int idPedido;

    @ManyToOne
    @JoinColumn(name="identidad_usuario")
    private Usuario usuario;

    @Column(nullable = false, length = 30)
    private String nombre;

    @Column(nullable = false, length = 40)
    private String apellidos;

    @Column(nullable = false, length = 80)
    private String direccion;
    
    @Column(length = 40)
    private String pais;

    @Column(length = 30)
    private String ciudad;
    
    @Column(name = "numero_telefono_movil", length = 20)
    private String numeroTelefonoMovil;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_envio")
    private MetodoEnvio metodoEnvio;  // EstadoPedido es un enum.
    
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private EstadoPedido estado;  // EstadoPedido es un enum.

    //Campos ni insertables ni actualizables directamente ---------------------------*/
    
    @Column(name = "precio_subtotal", updatable = false)
    private Double precioSubtotal;
    
    @Column(name = "gastos_envio", updatable = false)
    private Double gastosEnvio;
    
    @Column(name = "precio_total", updatable = false)
    private Double precioTotal;  // Es típico usar BigDecimal para manejo de dinero pero no lo hacemos aquí por ser más fácil realizar cálculos con double
	
    @Column(name = "fecha_pedido", nullable = false, updatable = false, insertable = false)
    private LocalDateTime fechaPedido;
    
    @Column(name = "fecha_actualizacion", nullable = false, updatable = false, insertable = false)
    private LocalDateTime fechaActualizacion;
  
    @OneToMany(mappedBy = "pedido")
    private List<LineaFacturacion> lineasFacturacion;
    
    //Opción recomendada: private Set<LineaFacturacion> lineasFacturacion;
    
    public enum MetodoEnvio {
        Recogida_en_tienda, CTT_Express, NACEX;
    }
    
    public enum EstadoPedido {
        pendiente, enviado, entregado, cancelado;
    }
    
}