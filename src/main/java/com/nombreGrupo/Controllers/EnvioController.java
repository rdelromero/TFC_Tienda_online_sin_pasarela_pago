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

import com.nombreGrupo.modelo.dto.EnvioDtoActualizacion;
import com.nombreGrupo.modelo.dto.EnvioDtoCreacion;
import com.nombreGrupo.modelo.entities.Envio;
import com.nombreGrupo.modelo.entities.Pedido;
import com.nombreGrupo.services.EnvioService;
import com.nombreGrupo.services.PedidoService;

@Controller
@RequestMapping("/pedidos/envios")
public class EnvioController {

	@Autowired
    private EnvioService envioService;
	@Autowired
    private PedidoService pedidoService;
	@Autowired
	private ModelMapper modeloMapper;
	
	@GetMapping
    public String getIndex(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "15") int size) {
		Pageable pageable = PageRequest.of(page, size);
	    Page<Envio> paginaEnvios = envioService.encontrarTodos(pageable);
	    model.addAttribute("paginaEnviosHtml", paginaEnvios);
        return "envios/todos";
    }
	
    @GetMapping("/{id}")
    public String getProducto(@PathVariable("id") int idEnvio, Model model) {
        Envio envio = envioService.encontrarPorId(idEnvio);
        if (envio == null) {
            return "redirect:/mostrarUno";
        }
        model.addAttribute("envio", envio);
        return "envios/mostrarUno";  // Vista para mostrar los detalles del producto
    }
    
    @GetMapping("/crear")
    public String getCrear(Model model) {
        // Suponiendo que hay un método en PedidoService que retorna solo los pedidos pendientes
        List<Pedido> pedidosPendientes = pedidoService.encontrarPorEstado(Pedido.EstadoPedido.pendiente);
        model.addAttribute("pedidosPendientes", pedidosPendientes);
        model.addAttribute("envioDto", new EnvioDtoCreacion()); // Asegúrate que EnvioDtoCreacion tenga un campo idPedido
        return "envios/crearUno";
    }
	
    @PostMapping("/crear")
    public String postStore(@ModelAttribute EnvioDtoCreacion envioDtoCreacion) {
    	envioService.crearYGuardar(envioDtoCreacion);
        return "redirect:/pedidos/envios";
    }
    
    @GetMapping("/editar/{id}")
    public String getEdit(@PathVariable("id") int id, Model model) {
        Envio envio = envioService.encontrarPorId(id);
        EnvioDtoActualizacion envioDtoActualizacion = new EnvioDtoActualizacion();
        //Relleno los valores de los campos de productoDtoCreacion con sus análogos en producto
        modeloMapper.map(envio, envioDtoActualizacion);
        model.addAttribute("idHtml", id);
        model.addAttribute("envioDto", envioDtoActualizacion);
        return "envios/editarUno";
    }
    
    @PostMapping("/editar/{id}")
    public String postUpdate(@PathVariable("id") int id, @ModelAttribute EnvioDtoActualizacion envioDtoActualizacion) {
        envioService.actualizar(id, envioDtoActualizacion);
        return "redirect:/pedidos/envios";
    }
}
