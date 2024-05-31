package com.nombreGrupo.Controllers;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.nombreGrupo.modelo.dto.ProductoDtoCreacion;
import com.nombreGrupo.modelo.entities.Producto;
import com.nombreGrupo.modelo.entities.Resena;
import com.nombreGrupo.modelo.entities.Fabricante;
import com.nombreGrupo.modelo.entities.Imagen;
import com.nombreGrupo.modelo.entities.Subcategoria;
import com.nombreGrupo.services.ProductoService;
import com.nombreGrupo.services.FabricanteService;
import com.nombreGrupo.services.LineaFacturacionService;
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
    private LineaFacturacionService lineaFacturacionService;
	@Autowired
	private ModelMapper modeloMapper;
	
    //private static final String UPLOADED_FOLDER = "src/main/resources/static/imagenes/productos/";
	
    @GetMapping
    public String getIndex(Model model, 
                           @RequestParam(defaultValue = "0") int page, 
                           @RequestParam(defaultValue = "15") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Producto> paginaProductos = productoService.encontrarTodos(pageable);
        
        Map<Integer, Integer> totalUnidadesVendidasMap = new HashMap<>();
        for (Producto producto : paginaProductos) {
            int totalUnidadesVendidas = lineaFacturacionService.encontrarNumeroUnidadesVendidasPorIdProducto(producto.getIdProducto());
            totalUnidadesVendidasMap.put(producto.getIdProducto(), totalUnidadesVendidas);
        }

        model.addAttribute("paginaProductos", paginaProductos);
        model.addAttribute("totalUnidadesVendidasMap", totalUnidadesVendidasMap);
        
        return "productos/todos";
    }
    
    @GetMapping("/{id}")
    public String getShow(@PathVariable("id") int idProducto, Model model) {
        Producto producto = productoService.encontrarPorId(idProducto);
        if (producto == null) {
            return "redirect:/mostrarUno";
        }
        List<Imagen> imagenes = productoService.encontrarImagenesPorIdProducto(idProducto);
        List<Resena> resenas = productoService.encontrarResenasPorIdProducto(idProducto);
        //Primero las resenas más recientes
        resenas.sort(Comparator.comparingInt(Resena::getIdResena).reversed());
        int numeroUnidadesVendidas = lineaFacturacionService.encontrarNumeroUnidadesVendidasPorIdProducto(producto.getIdProducto());
        model.addAttribute("producto", producto);
        model.addAttribute("imagenes", imagenes); // Agregar la lista de imágenes al modelo
        model.addAttribute("resenasHtml", resenas);
        model.addAttribute("numeroUnidadesVendidasHtml", numeroUnidadesVendidas);
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
        String nombre = productoService.encontrarPorId(idProducto).getNombre();
        model.addAttribute("imagenes", imagenes); // Agregar la lista de imágenes al modelo
        model.addAttribute("nombreHtml", nombre); 
        return "productos/imagenes"; // Retorna el nombre de la vista que muestra las imágenes
    }
    
    @GetMapping("/{idProducto}/imagenes-v")
    public String verImagenesProductoV(@PathVariable int idProducto, Model model) {
        // Obtener las imágenes asociadas con el producto utilizando el servicio
        List<Imagen> imagenes = productoService.encontrarImagenesPorIdProducto(idProducto);
        String nombre = productoService.encontrarPorId(idProducto).getNombre();
        model.addAttribute("imagenes", imagenes); // Agregar la lista de imágenes al modelo
        model.addAttribute("nombreHtml", nombre); 
        return "productos/slideshow_vertical"; // Retorna el nombre de la vista que muestra las imágenes
    }
    
    @GetMapping("/{idProducto}/resenas")
    public String getResenasPorIdProducto(@PathVariable int idProducto, Model model) {
        // Obtener las imágenes asociadas con el producto utilizando el servicio
        List<Resena> resenas = productoService.encontrarResenasPorIdProducto(idProducto);
        resenas.sort(Comparator.comparingInt(Resena::getIdResena).reversed());
        String nombre = productoService.encontrarPorId(idProducto).getNombre();
        model.addAttribute("resenasHtml", resenas); // Agregar la lista de imágenes al modelo
        model.addAttribute("nombreHtml", nombre); 
        return "productos/resenas"; // Retorna el nombre de la vista que muestra las imágenes
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
