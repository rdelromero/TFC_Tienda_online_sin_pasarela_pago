package com.nombreGrupo.modelo.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FabricanteDtoCreacion {

    private int idFabricante;
    private String nombre;
    private LocalDateTime fechaFundacion;
    private String pais;
    private String paginaWeb;
    private String descripcion;
    private String imagenUrl;
}
