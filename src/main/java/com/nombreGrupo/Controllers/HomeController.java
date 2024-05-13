package com.nombreGrupo.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping
    public String getWelcomeHome() {
	    return "index";
    }
	
}
