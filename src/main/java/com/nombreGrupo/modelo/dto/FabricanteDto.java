package com.nombreGrupo.modelo.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FabricanteDto {

    private int idFabricante;
    private String nombre;
    private LocalDateTime fechaFundacion;
    private String pais;
    private String paginaWeb;
    private String descripcion;
    private String imagenUrl;
    private List<ProductoDto> productosDto;
}
