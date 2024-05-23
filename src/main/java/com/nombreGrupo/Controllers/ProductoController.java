package com.nombreGrupo.Controllers;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import com.nombreGrupo.modelo.dto.ProductoDtoCreacion;
import com.nombreGrupo.modelo.entities.Producto;
import com.nombreGrupo.modelo.entities.Fabricante;
import com.nombreGrupo.modelo.entities.Imagen;
import com.nombreGrupo.modelo.entities.Subcategoria;
import com.nombreGrupo.repositories.ImagenRepository;
import com.nombreGrupo.services.ProductoService;
import com.nombreGrupo.services.FabricanteService;
import com.nombreGrupo.services.SubcategoriaService;

@Controller
@RequestMapping("/productos")
public class ProductoController {
	
	@Autowired
    private ProductoService productoService;
	@Autowired
    private FabricanteService fabricanteService;
	@Autowired
    private SubcategoriaService subcategoriaService;
	@Autowired
	private ModelMapper modeloMapper;
	
	@Autowired
    private ImagenRepository imagenRepository;

    private static final String UPLOADED_FOLDER = "src/main/resources/static/imagenes/productos/";
	
	@GetMapping
    public String getIndex(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "15") int size) {
		Pageable pageable = PageRequest.of(page, size);
	    Page<Producto> paginaProductos = productoService.encontrarTodos(pageable);
	    model.addAttribute("paginaProductos", paginaProductos);
        return "productos/todos";
    }
    
    @GetMapping("/{id}")
    public String getShow(@PathVariable("id") int idProducto, Model model) {
        Producto producto = productoService.encontrarPorId(idProducto);
        if (producto == null) {
            return "redirect:/mostrarUno";
        }
        model.addAttribute("producto", producto);
        return "productos/mostrarUno";  // Vista para mostrar los detalles del producto
    }
	
    @GetMapping("/buscar2")
    public String getProductosPorPalabrasClave2(@RequestParam("query") String query, Model model) {
        List<Producto> productos = productoService.buscarPorPalabrasClave2(query);
        model.addAttribute("productosHtml", productos);
        return "productos/busqueda";
    }
    
    @GetMapping("/{idProducto}/imagenes")
    public String verImagenesProducto(@PathVariable int idProducto, Model model) {
        // Obtener las imágenes asociadas con el producto utilizando el servicio
        List<Imagen> imagenes = productoService.encontrarImagenesPorIdProducto(idProducto);
        model.addAttribute("imagenes", imagenes); // Agregar la lista de imágenes al modelo
        model.addAttribute("idProducto", idProducto); // Opcional: agregar el ID del producto al modelo
        return "productos/imagenes"; // Retorna el nombre de la vista que muestra las imágenes
    }
    
	@GetMapping("/crear")
    public String getCrear(Model model) {
		model.addAttribute("productoDto", new ProductoDtoCreacion());
        List<Fabricante> fabricantes = fabricanteService.encontrarTodos();
        List<Subcategoria> subcategorias = subcategoriaService.encontrarTodas();
        model.addAttribute("fabricantes", fabricantes);
        model.addAttribute("subcategorias", subcategorias);
        return "productos/crearUno";
    }
	
    @PostMapping("/crear")
    public String postStore(@ModelAttribute ProductoDtoCreacion productoDtoCreacion) {
    	productoService.crearYGuardar(productoDtoCreacion);
        return "redirect:/productos";
    }
    
    @GetMapping("/editar/{id}")
    public String getEdit(@PathVariable("id") int id, Model model) {
        Producto producto = productoService.encontrarPorId(id);
        ProductoDtoCreacion productoDtoCreacion = new ProductoDtoCreacion();
        //Relleno los valores de los campos de productoDtoCreacion con sus análogos en producto
        modeloMapper.map(producto, productoDtoCreacion);
        List<Fabricante> fabricantes = fabricanteService.encontrarTodos();
        List<Subcategoria> subcategorias = subcategoriaService.encontrarTodas();
        model.addAttribute("idHtml", id);
        model.addAttribute("productoDto", productoDtoCreacion);
        model.addAttribute("fabricantes", fabricantes);
        model.addAttribute("subcategorias", subcategorias);
        return "productos/editarUno";
    }
    
    @PostMapping("/editar/{id}")
    public String postUpdate(@PathVariable("id") int id, @ModelAttribute ProductoDtoCreacion productoDto) {
        productoService.actualizar(id, productoDto);
        return "redirect:/productos";
    }
    
    @GetMapping("/borrar/{id}")
    public String getDestroy(@PathVariable("id") int id) {
        productoService.borrarPorId(id);
        return "redirect:/productos";
    }
}
