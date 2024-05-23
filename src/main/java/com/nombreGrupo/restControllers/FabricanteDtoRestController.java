package com.nombreGrupo.restControllers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nombreGrupo.modelo.dto.FabricanteDto;
import com.nombreGrupo.modelo.dto.ProductoDto;
import com.nombreGrupo.modelo.entities.Fabricante;
import com.nombreGrupo.services.FabricanteService;

import jakarta.persistence.EntityNotFoundException;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/fabricantesdto")
public class FabricanteDtoRestController {

	@Autowired
    private FabricanteService fabricanteService;
	
	@Autowired
    private ModelMapper modelMapper;
	
    @GetMapping
    public ResponseEntity<List<FabricanteDto>> getIndexConProductos() {
        // Obtener la lista de fabricantes desde el servicio
        List<Fabricante> fabricantes = fabricanteService.encontrarTodos();

        // Convertir la lista de Fabricante a FabricanteDtoFront
        List<FabricanteDto> fabricanteDtos = fabricantes.stream()
            .map(fabricante -> {
                FabricanteDto dto = modelMapper.map(fabricante, FabricanteDto.class);
                List<ProductoDto> productosDto = fabricante.getProductos().stream()
                    .map(producto -> modelMapper.map(producto, ProductoDto.class))
                    .collect(Collectors.toList());
                dto.setProductosDto(productosDto);
                return dto;
            })
            .collect(Collectors.toList());

        // Devolver la lista de DTOs
        return ResponseEntity.ok(fabricanteDtos);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getShowPorIdConProductos(@PathVariable int id) {
        // Obtener el fabricante por ID desde el servicio
    	try {
    		Fabricante fabricante = fabricanteService.encontrarPorId(id);
            // Convertir el Fabricante a FabricanteDtoFront, incluyendo los productos
            FabricanteDto fabricanteDto = modelMapper.map(fabricante, FabricanteDto.class);
            List<ProductoDto> productosDto = fabricante.getProductos().stream()
                .map(producto -> modelMapper.map(producto, ProductoDto.class))
                .collect(Collectors.toList());
            fabricanteDto.setProductosDto(productosDto);

            // Devolver DTO
            return ResponseEntity.ok(fabricanteDto);
    	} catch (EntityNotFoundException ex) {
        	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", ex.getMessage()));
        }
    }
    	
    @GetMapping("/nombre/{nombreFabricante}")
    public ResponseEntity<?> getShowPorNombreConProductos(@PathVariable String nombreFabricante) {
        try {
            Fabricante fabricante = fabricanteService.encontrarPorNombre(nombreFabricante);
            // Convertir el Fabricante a FabricanteDtoFront, incluyendo los productos
            FabricanteDto fabricanteDto = modelMapper.map(fabricante, FabricanteDto.class);
            List<ProductoDto> productosDto = fabricante.getProductos().stream()
                .map(producto -> modelMapper.map(producto, ProductoDto.class))
                .collect(Collectors.toList());
            fabricanteDto.setProductosDto(productosDto);

            // Devolver DTO
            return ResponseEntity.ok(fabricanteDto);
    	} catch (EntityNotFoundException ex) {
        	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", ex.getMessage()));
        }
    }
}
