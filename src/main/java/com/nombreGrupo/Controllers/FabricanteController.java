package com.nombreGrupo.Controllers;

import java.util.List;

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
import com.nombreGrupo.modelo.dto.FabricanteDtoCreacion;
import com.nombreGrupo.modelo.dto.ProductoDtoCreacion;
import com.nombreGrupo.modelo.entities.Fabricante;
import com.nombreGrupo.modelo.entities.Subcategoria;
import com.nombreGrupo.services.FabricanteService;


@Controller
@RequestMapping("/fabricantes")
public class FabricanteController {

	@Autowired
    private FabricanteService fabricanteService;
	
	@Autowired
	private ModelMapper modeloMapper;
	
	@GetMapping
    public String getIndex(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "15") int size) {
		Pageable pageable = PageRequest.of(page, size);
	    Page<Fabricante> paginaFabricantes = fabricanteService.encontrarTodosPaginacion(pageable);
	    model.addAttribute("paginaFabricantesHtml", paginaFabricantes);
        return "fabricantes/todos";
    }
	
    @GetMapping("/{id}")
    public String getShow(@PathVariable("id") int idFabricante, Model model) {
        Fabricante fabricante = fabricanteService.encontrarPorId(idFabricante);
        if (fabricante == null) {
            return "redirect:/mostrarUno";
        }
        model.addAttribute("fabricante", fabricante);
        return "fabricantes/mostrarUno";  // Vista para mostrar los detalles del producto
    }
    
	@GetMapping("/crear")
    public String getCrear(Model model) {
		FabricanteDtoCreacion fabricantedtocreacion = new FabricanteDtoCreacion();
		model.addAttribute("fabricanteDto", fabricantedtocreacion);
        return "fabricantes/crearUno";
    }
	
	@PostMapping("/crear")
	public String postStore(@ModelAttribute FabricanteDtoCreacion fabricantedtocreacion) {
		fabricanteService.crearYGuardar(fabricantedtocreacion);
		return "redirect:/fabricantes";
	}

    @GetMapping("/editar/{id}")
    public String getEdit(@PathVariable("id") int idFabricante, Model model) {
        Fabricante fabricante = fabricanteService.encontrarPorId(idFabricante);
        FabricanteDtoCreacion fabricanteDtoActualizacion = new FabricanteDtoCreacion();
        //Relleno los valores de los campos de fabricanteDtoCreacion con sus análogos en fabricante
        modeloMapper.map(fabricante, fabricanteDtoActualizacion);
        model.addAttribute("id", idFabricante);
        model.addAttribute("fabricanteDto", fabricanteDtoActualizacion);
        return "fabricantes/editarUno";
    }
    
    @PostMapping("/editar/{id}")
    public String postUpdate(@PathVariable("id") int idFabricante, @ModelAttribute FabricanteDtoCreacion fabricanteDto) {
       fabricanteService.actualizar(idFabricante, fabricanteDto);
        return "redirect:/fabricantes";
    }
    
    @GetMapping("/borrar/{id}")
    public String getDestroy(@PathVariable("id") int id) {
        fabricanteService.borrarPorId(id);
        return "redirect:/fabricantes";
    }
    
    /*REVISAR ESTE MÉTODO
    @GetMapping("/borrar-cascada/{id}")
    public String getDestroyCascada(@PathVariable("id") int idFabricante) {
    	fabricanteService.eliminarFabricanteConProductos(idFabricante);
        return "redirect:/fabricantes";
    }*/
}
