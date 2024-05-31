package com.nombreGrupo.Controllers;

import java.util.HashMap;
import java.util.Map;

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

import com.nombreGrupo.modelo.dto.UsuarioDtoRegistro;
import com.nombreGrupo.modelo.entities.Usuario;
import com.nombreGrupo.services.UsuarioService;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {
	
	@Autowired
    private UsuarioService usuarioService;
	
	@GetMapping
    public String getIndex(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "15") int size) {
		Pageable pageable = PageRequest.of(page, size);
	    Page<Usuario> paginaUsuarios = usuarioService.encontrarTodosPaginacion(pageable);
	    
        Map<Integer, Integer> numeroPedidosPorIdUsuarioMap = new HashMap<>();
        for (Usuario usuario : paginaUsuarios) {
            int numeroPedidosPorIdUsuario = usuarioService.encontrarPedidosPorUsuario_IdUsuario(usuario.getIdUsuario()).size();
            numeroPedidosPorIdUsuarioMap.put(usuario.getIdUsuario(), numeroPedidosPorIdUsuario);
        }
	    model.addAttribute("paginausuarios", paginaUsuarios);
	    model.addAttribute("numeroPedidosPorIdUsuarioMap", numeroPedidosPorIdUsuarioMap);
        return "usuarios/todos";
    }
    
    @GetMapping("/{id}")
    public String getUsuario(@PathVariable("id") int idUsuario, Model model) {
    	Usuario usuario = usuarioService.encontrarPorId(idUsuario);
      
    	//MIRAR ESTO DE AQII
        if (usuario == null) {
            return "redirect:/usuarios";
        }
        int numeroPedidos = usuarioService.encontrarPedidosPorUsuario_IdUsuario(idUsuario).size();
        model.addAttribute("usuario", usuario);
        model.addAttribute("numeroPedidosHtml", numeroPedidos);
        return "usuarios/mostrarUno";  // Vista para mostrar los detalles del usuario
    }
	
    @GetMapping("/{id}/pedidos")
    public String getPedidosPorIdUsuario(@PathVariable("id") int idUsuario, Model model) {
    	model.addAttribute("idUsuarioHtml", idUsuario);
    	model.addAttribute("pedidosPorIdUsuarioHtml", usuarioService.encontrarPedidosPorUsuario_IdUsuario(idUsuario));
        return "usuarios/pedidosPorIdUsuario";
    }
	
    @GetMapping("/crearadmin")
    public String showCreateAdminForm(Model model) {
        model.addAttribute("usuarioDto", new UsuarioDtoRegistro());
        return "usuarios/crearadmin";
    }

    @PostMapping("/crearadmin")
    public String createAdmin(@ModelAttribute("usuarioDto") UsuarioDtoRegistro usuarioDto) {
        usuarioService.crearAdmin(usuarioDto);
        return "redirect:/usuarios";  // Redirige a la lista de usuarios o donde sea necesario
    }
    
    //Se podrá borrar el usuario si NO tiene ni pedidos ni reseñas a su nombre
    @GetMapping("/borrar/{id}")
    public String getDestroy(@PathVariable("id") int idUsuario) {
    	usuarioService.borrarPorId(idUsuario);
        return "redirect:/usuarios";
    }
    
    
}

