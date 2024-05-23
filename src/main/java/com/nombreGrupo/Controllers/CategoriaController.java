package com.nombreGrupo.Controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.nombreGrupo.modelo.dto.CategoriaDtoCreacion;
import com.nombreGrupo.modelo.entities.Categoria;
import com.nombreGrupo.services.CategoriaService;


@Controller
@RequestMapping("/categorias")
public class CategoriaController {

	@Autowired
    private CategoriaService categoriaService;
	
	@Autowired
	private ModelMapper modeloMapper;
	
    @GetMapping
    public String getIndex(Model model) {
        model.addAttribute("categoriasHtml", categoriaService.encontrarTodas());
        return "categorias/todas";
    }
	
    @GetMapping("/{id}")
    public String getCategoria(@PathVariable("id") int idCategoria, Model model) {
        Categoria categoria = categoriaService.encontrarPorId(idCategoria); 	
        if (categoria == null) {
            return "redirect:/mostrarUno";
        }
        model.addAttribute("categoria", categoria);
        return "categorias/mostrarUno";
    }
    
	@GetMapping("/crear")
    public String getCrear(Model model) {
		CategoriaDtoCreacion categoriadtocreacion = new CategoriaDtoCreacion();
		model.addAttribute("categoriaDto", categoriadtocreacion);
        return "categorias/crearUno";
    }
	
	@PostMapping("/crear")
	public String postStore(@ModelAttribute CategoriaDtoCreacion categoriadtocreacion) {
		categoriaService.crearYGuardar(categoriadtocreacion);
		return "redirect:/categorias";

	}

    @GetMapping("/editar/{id}")
    public String getEdit(@PathVariable("id") int idCategoria, Model model) {
        Categoria categoria = categoriaService.encontrarPorId(idCategoria);
         CategoriaDtoCreacion categoriadtoactualizacion = new CategoriaDtoCreacion();
        //Relleno los valores de los campos de categoriaDtoCreacion con sus análogos en categoría
        modeloMapper.map(categoria, categoriadtoactualizacion);
        model.addAttribute("id", idCategoria);
        model.addAttribute("categoriaDto", categoriadtoactualizacion);
        return "categorias/editarUno";
    }
    
    @PostMapping("/editar/{id}")
    public String postUpdate(@PathVariable("id") int idCategoria, @ModelAttribute CategoriaDtoCreacion categoriaDto) {
       categoriaService.actualizar(idCategoria, categoriaDto);
        return "redirect:/categorias";
    }
    
    @GetMapping("/borrar/{id}")
    public String getDestroy(@PathVariable("id") int idCategoria) {
    	categoriaService.borrarPorId(idCategoria);
        return "redirect:/categorias";
    }
}
