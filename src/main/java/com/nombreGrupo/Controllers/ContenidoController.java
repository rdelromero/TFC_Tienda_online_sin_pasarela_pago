package com.nombreGrupo.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/contenido")
public class ContenidoController {
	
    @GetMapping("/faq")
    public String getFAQ() {
         return "misc/FAQ";
    }
    
    @GetMapping("/legislacion")
    public String getLegislacion() {
         return "misc/legislacion";
    }
    
    @GetMapping("/politicaenvios")
    public String getPoliticaEnvios() {
         return "misc/politicadeenvios";
    }
    
    @GetMapping("/politicadevoluciones")
    public String getPoliticaDevoluciones() {
         return "misc/politicadevoluciones";
    }

}
