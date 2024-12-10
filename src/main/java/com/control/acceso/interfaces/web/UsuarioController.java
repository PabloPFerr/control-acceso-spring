package com.control.acceso.interfaces.web;

import com.control.acceso.domain.model.Usuario;
import com.control.acceso.application.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/nuevo")
    public String crearUsuario(Usuario usuario, RedirectAttributes redirectAttributes) {
        try {
            logger.info("Recibiendo petición para crear usuario: email={}, role={}", usuario.getEmail(), usuario.getRole());
            usuarioService.crearUsuario(usuario);
            logger.info("Usuario creado exitosamente: email={}, role={}", usuario.getEmail(), usuario.getRole());
            redirectAttributes.addFlashAttribute("mensaje", "Usuario creado exitosamente");
        } catch (Exception e) {
            logger.error("Error al crear usuario: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/dashboard/admin";
    }

    @PostMapping("/eliminar/{id}")
    @ResponseBody
    public ResponseEntity<String> eliminarUsuario(@PathVariable Long id) {
        try {
            logger.info("Recibiendo petición para eliminar usuario con ID: {}", id);
            usuarioService.eliminarUsuario(id);
            logger.info("Usuario eliminado exitosamente: id={}", id);
            return ResponseEntity.ok("Usuario eliminado exitosamente");
        } catch (Exception e) {
            logger.error("Error al eliminar usuario: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
