package com.nombreGrupo.modelo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class PedidoDtoCreacion{

	private int idPedido;
	
    //@NotNull(message = "El ID del usuario es obligatorio")
    private int idUsuario;

    //@NotBlank(message = "El nombre no puede estar vacío")
    //@Size(max = 30, message = "El nombre no puede tener más de 50 caracteres")
    private String nombre;

    //@NotBlank(message = "Los apellidos no pueden estar vacíos")
    //@Size(max = 40, message = "Los apellidos no pueden tener más de 50 caracteres")
    private String apellidos;

    //@NotBlank(message = "La dirección no puede estar vacía")
    //@Size(max = 80, message = "La dirección no puede tener más de 100 caracteres")
    private String direccion;

    //@NotBlank(message = "El país no puede estar vacío")
    //@Size(max = 40, message = "El país no puede tener más de 50 caracteres")
    private String pais;

    //@NotBlank(message = "La ciudad no puede estar vacía")
    //@Size(max = 30, message = "La ciudad no puede tener más de 50 caracteres")
    private String ciudad;

    //@NotBlank(message = "El número de teléfono móvil no puede estar vacío")
    //@Pattern(regexp = "^\\+?[0-9. ()-]{7,}$", message = "El número de teléfono móvil no es válido")
    //@Size(max = 20, message = "El número de teléfono móvil no puede tener más de 20 caracteres")
    private String numeroTelefonoMovil;
}
