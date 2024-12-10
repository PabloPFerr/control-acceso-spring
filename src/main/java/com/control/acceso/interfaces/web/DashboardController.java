package com.control.acceso.interfaces.web;

import com.control.acceso.domain.port.incoming.UsuarioManagementUseCase;
import com.control.acceso.interfaces.dto.RegistroDTO;
import com.control.acceso.interfaces.dto.RegistroManualDTO;
import com.control.acceso.domain.model.Registro;
import com.control.acceso.domain.model.Usuario;
import com.control.acceso.domain.port.incoming.RegistroManagementUseCase;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final UsuarioManagementUseCase usuarioManagementUseCase;
    private final RegistroManagementUseCase registroManagementUseCase;

    public DashboardController(UsuarioManagementUseCase usuarioManagementUseCase, 
                             RegistroManagementUseCase registroManagementUseCase) {
        this.usuarioManagementUseCase = usuarioManagementUseCase;
        this.registroManagementUseCase = registroManagementUseCase;
    }

    @GetMapping("")
    public String dashboard(Model model) {
        Usuario usuario = obtenerUsuarioActual();
        List<Registro> registros = registroManagementUseCase.obtenerRegistrosUsuario(usuario);
        List<RegistroDTO> registrosDTO = registros.stream()
                .map(RegistroDTO::fromRegistro)
                .collect(Collectors.toList());
        
        model.addAttribute("usuario", usuario);
        model.addAttribute("registros", registrosDTO);
        model.addAttribute("tieneRegistroAbierto", registroManagementUseCase.tieneRegistroAbierto(usuario));
        return "dashboard";
    }

    @PostMapping("/entrada")
    public String registrarEntrada(RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = obtenerUsuarioActual();
            registroManagementUseCase.registrarEntrada(usuario);
            redirectAttributes.addFlashAttribute("mensaje", "Entrada registrada exitosamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/salida")
    public String registrarSalida(RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = obtenerUsuarioActual();
            registroManagementUseCase.registrarSalida(usuario);
            redirectAttributes.addFlashAttribute("mensaje", "Salida registrada exitosamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/admin")
    public String adminDashboard(Model model) {
        Usuario usuarioActual = obtenerUsuarioActual();
        if (!usuarioActual.getRole().equals(Usuario.Role.ADMIN)) {
            return "redirect:/dashboard";
        }
        
        List<Usuario> usuarios = usuarioManagementUseCase.obtenerTodosLosUsuarios();
        LocalDateTime inicioHoy = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime finHoy = inicioHoy.plusDays(1);
        List<Registro> registrosHoy = registroManagementUseCase.obtenerRegistrosPeriodo(inicioHoy, finHoy);
        
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("registros", registrosHoy);
        return "admin/dashboard";
    }

    @GetMapping("/reportes")
    public String mostrarReportes(Model model) {
        List<Registro> registros = registroManagementUseCase.obtenerTodosLosRegistros();
        List<RegistroDTO> registrosDTO = registros.stream()
                .map(RegistroDTO::fromRegistro)
                .collect(Collectors.toList());
        model.addAttribute("registros", registrosDTO);
        return "admin/reportes";
    }

    @PostMapping("/registro-manual")
    public String registrarManual(@ModelAttribute RegistroManualDTO registroManualDTO, 
                                RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = obtenerUsuarioActual();
            registroManagementUseCase.registrarManualEditado(
                usuario,
                registroManualDTO.getEntradaDateTime(),
                registroManualDTO.getSalidaDateTime()
            );
            redirectAttributes.addFlashAttribute("mensaje", "Registro manual creado exitosamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/dashboard";
    }

    private Usuario obtenerUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return usuarioManagementUseCase.obtenerUsuarioPorEmail(auth.getName());
    }
}
