package com.control.acceso.interfaces.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login"; // Asegúrate de tener un archivo login.html en tu directorio de plantillas
    }
}
