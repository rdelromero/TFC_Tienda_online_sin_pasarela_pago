package com.nombreGrupo.Controllers;

import java.util.Arrays;
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

import com.nombreGrupo.modelo.dto.PedidoDtoActualizacionSinCambiarLineasFacturacion;
import com.nombreGrupo.modelo.dto.ProductoDtoCreacion;
import com.nombreGrupo.modelo.entities.Fabricante;
import com.nombreGrupo.modelo.entities.Pedido;
import com.nombreGrupo.modelo.entities.Producto;
import com.nombreGrupo.modelo.entities.Subcategoria;
import com.nombreGrupo.services.PedidoService;


@Controller
@RequestMapping("/pedidos")
public class PedidoController {

	@Autowired
    private PedidoService pedidoService;
	
	@Autowired
	private ModelMapper modeloMapper;
	
	@GetMapping
    public String getIndex(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "15") int size) {
		Pageable pageable = PageRequest.of(page, size);
	    Page<Pedido> paginaPedidos = pedidoService.encontrarTodos(pageable);
	    model.addAttribute("paginaPedidosHtml", paginaPedidos);
        return "pedidos/todos";
    }
	
    @GetMapping("/{id}")
    public String getShow(@PathVariable("id") int idPedido, Model model) {
        Pedido pedido = pedidoService.encontrarPorId(idPedido);
        if (pedido == null) {
            return "redirect:/mostrarUno";
        }
        model.addAttribute("pedido", pedido);
        return "pedidos/mostrarUno";  // Vista para mostrar los detalles del producto
    }
    
    @GetMapping("/editar/{id}")
    public String getEdit(@PathVariable("id") int id, Model model) {
        Pedido pedido = pedidoService.encontrarPorId(id);
        PedidoDtoActualizacionSinCambiarLineasFacturacion pedidoDtoActualizacionSinCambiarLF = new PedidoDtoActualizacionSinCambiarLineasFacturacion();
        //Relleno los valores de los campos de productoDtoCreacion con sus análogos en producto
        modeloMapper.map(pedido, pedidoDtoActualizacionSinCambiarLF);
        model.addAttribute("idHtml", id);
        model.addAttribute("pedidoDto", pedidoDtoActualizacionSinCambiarLF);
        List<String> paises = Arrays.asList("España", "Alemania", "Austria", "España", "Francia", "Irlanda", "Italia", "Portugal", "Reino Unido GBIN", "Suiza");
        model.addAttribute("paises", paises);
        return "pedidos/editarUno";
    }
    
    @PostMapping("/editar/{id}")
    public String postUpdate(@PathVariable("id") int id, @ModelAttribute PedidoDtoActualizacionSinCambiarLineasFacturacion pedidoDto) {
        pedidoService.actualizarSinCambiarLF(id, pedidoDto);
        return "redirect:/pedidos";
    }
}
