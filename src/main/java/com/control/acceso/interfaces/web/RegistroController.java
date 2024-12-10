package com.control.acceso.interfaces.web;

import com.control.acceso.domain.model.Usuario;
import com.control.acceso.domain.port.incoming.RegistroManagementUseCase;
import com.control.acceso.domain.port.incoming.UsuarioManagementUseCase;
import com.control.acceso.interfaces.dto.RegistroManualRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/registro")
public class RegistroController {

    private final UsuarioManagementUseCase usuarioManagementUseCase;
    private final RegistroManagementUseCase registroManagementUseCase;

    @Autowired
    public RegistroController(UsuarioManagementUseCase usuarioManagementUseCase,
                            RegistroManagementUseCase registroManagementUseCase) {
        this.usuarioManagementUseCase = usuarioManagementUseCase;
        this.registroManagementUseCase = registroManagementUseCase;
    }

    @PostMapping("/entrada")
    public String registrarEntrada() {
        Usuario usuario = obtenerUsuarioActual();
        registroManagementUseCase.registrarEntrada(usuario);
        return "redirect:/dashboard";
    }

    @PostMapping("/salida")
    public String registrarSalida() {
        Usuario usuario = obtenerUsuarioActual();
        registroManagementUseCase.registrarSalida(usuario);
        return "redirect:/dashboard";
    }

    @GetMapping("/manual")
    public String mostrarFormularioManual(Model model) {
        model.addAttribute("usuarios", usuarioManagementUseCase.obtenerTodosLosUsuarios());
        return "registro/manual";
    }

    @PostMapping("/manual")
    public String registrarManual(@ModelAttribute RegistroManualRequestDTO requestDTO) {
        Usuario usuario = usuarioManagementUseCase.obtenerUsuarioPorId(requestDTO.getUsuarioId());
        registroManagementUseCase.registrarManualEditado(usuario, requestDTO.getEntrada(), requestDTO.getSalida());
        return "redirect:/admin/reportes";
    }

    @GetMapping("/informe")
    public String mostrarInforme(Model model,
                               @RequestParam(required = false) LocalDateTime inicio,
                               @RequestParam(required = false) LocalDateTime fin) {
        if (inicio != null && fin != null) {
            model.addAttribute("registros", registroManagementUseCase.obtenerRegistrosPeriodo(inicio, fin));
        }
        return "registro/informe";
    }

    private Usuario obtenerUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return usuarioManagementUseCase.obtenerUsuarioPorEmail(auth.getName());
    }
}
