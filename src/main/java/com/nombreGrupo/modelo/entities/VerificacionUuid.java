package com.nombreGrupo.modelo.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "verificacion_uuid")
public class VerificacionUuid {

    @Id
    @Column(name="id_verificacion_uuid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idVerificacionUuid;

    @Column(name="uuid", nullable = false)
    private String uuid;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "identidad_usuario")
    private Usuario usuario;

    @Column(name = "fecha_expiracion", nullable = false)
    private LocalDateTime fechaExpiracion;

    public VerificacionUuid() {}

    public VerificacionUuid(String uuid, Usuario usuario, LocalDateTime fechaExpiracion) {
        this.uuid = uuid;
        this.usuario = usuario;
        this.fechaExpiracion = fechaExpiracion;
    }
}