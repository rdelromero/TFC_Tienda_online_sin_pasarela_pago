package com.nombreGrupo.Controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.nombreGrupo.modelo.dto.SubcategoriaDtoCreacion;
import com.nombreGrupo.modelo.entities.Subcategoria;
import com.nombreGrupo.services.SubcategoriaService;


@Controller
@RequestMapping("/subcategorias")
public class SubcategoriaController {

	@Autowired
    private SubcategoriaService subcategoriaservice;
	
	@Autowired
	private ModelMapper modeloMapper;
	
	@GetMapping
    public String getIndex(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "15") int size) {
		Pageable pageable = PageRequest.of(page, size);
	    Page<Subcategoria> paginaSubcategorias = subcategoriaservice.encontrarTodasPaginacion(pageable);
	    model.addAttribute("paginaSubcategoriasHtml", paginaSubcategorias);
        return "subcategorias/todas";
    }
	
    @GetMapping("/{id}")
    public String getSubcategoria(@PathVariable("id") int idSubcategoria, Model model) {
        Subcategoria subcategoria = subcategoriaservice.encontrarPorId(idSubcategoria); 	
        if (subcategoria == null) {
            return "redirect:/mostrarUno";
        }
        model.addAttribute("subcategoria", subcategoria);
        return "subcategorias/mostrarUno";
    }
    
	@GetMapping("/crear")
    public String getCrear(Model model) {
		SubcategoriaDtoCreacion subcategoriadtocreacion = new SubcategoriaDtoCreacion();
		model.addAttribute("subcategoriaDto", subcategoriadtocreacion);
        return "subcategorias/crearUno";
    }
	
	@PostMapping("/crear")
	public String postStore(@ModelAttribute SubcategoriaDtoCreacion subcategoriadtocreacion) {
		subcategoriaservice.crearYGuardar(subcategoriadtocreacion);
		return "redirect:/subcategorias";

	}

    
    @GetMapping("/editar/{id}")
    public String getEdit(@PathVariable("id") int idSubcategoria, Model model) {
        Subcategoria subcategoria = subcategoriaservice.encontrarPorId(idSubcategoria);
         SubcategoriaDtoCreacion subcategoriadtoactualizacion = new SubcategoriaDtoCreacion();
        //Relleno los valores de los campos de productoDtoCreacion con sus análogos en producto
        modeloMapper.map(subcategoria, subcategoriadtoactualizacion);
        model.addAttribute("id", idSubcategoria);
        model.addAttribute("subcategoriaDto", subcategoriadtoactualizacion);
        return "subcategorias/editarUno";
    }
    
    @PostMapping("/editar/{id}")
    public String postUpdate(@PathVariable("id") int idSubcategoria, @ModelAttribute SubcategoriaDtoCreacion subcategoriaDto) {
       subcategoriaservice.actualizar(idSubcategoria, subcategoriaDto);
        return "redirect:/subcategorias";
    }
    
    @GetMapping("/borrar/{id}")
    public String getDestroy(@PathVariable("id") int idSubcategoria) {
    	subcategoriaservice.borrarPorId(idSubcategoria);

        return "redirect:/subcategorias";
    }
}
