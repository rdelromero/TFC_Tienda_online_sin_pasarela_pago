package com.nombreGrupo.restControllers;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nombreGrupo.modelo.dto.ProductoDto;
import com.nombreGrupo.modelo.dto.ImagenDto;
import com.nombreGrupo.modelo.entities.Producto;
import com.nombreGrupo.services.ProductoService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/productosdto")
public class ProductoDtoRestController {
	
	@Autowired
    private ProductoService productoService;
	
	@Autowired
    private ModelMapper modelMapper;
	
    @GetMapping
    public ResponseEntity<List<ProductoDto>> getIndexConProductos() {
        // Obtener la lista de productos desde el servicio
        List<Producto> productos = productoService.encontrarTodos();

        // Convertir la lista de producto a productoDtoFront
        List<ProductoDto> productoDtos = productos.stream()
            .map(producto -> {
                ProductoDto dto = modelMapper.map(producto, ProductoDto.class);
                List<ImagenDto> imagenesDto = producto.getImagenes().stream()
                    .map(imagen -> modelMapper.map(imagen, ImagenDto.class))
                    .collect(Collectors.toList());
                dto.setImagenesDto(imagenesDto);
                return dto;
            })
            .collect(Collectors.toList());

        // Devolver la lista de DTOs
        return ResponseEntity.ok(productoDtos);
    }
}
