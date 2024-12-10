package com.control.acceso.interfaces.web;

import com.control.acceso.application.service.ReporteService;
import com.control.acceso.application.service.UsuarioService;
import com.control.acceso.domain.model.Usuario;
import com.control.acceso.interfaces.dto.ReporteDTO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/reportes")
public class ReporteController {

    private final ReporteService reporteService;
    private final UsuarioService usuarioService;

    public ReporteController(ReporteService reporteService, UsuarioService usuarioService) {
        this.reporteService = reporteService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String mostrarReportes(
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuarioActual = usuarioService.obtenerUsuarioPorEmail(auth.getName());
        boolean esAdmin = usuarioActual.getRole() == Usuario.Role.ADMIN;

        // Si no se especifican fechas, usar el mes actual
        if (fechaInicio == null) {
            fechaInicio = LocalDate.now().withDayOfMonth(1);
        }
        if (fechaFin == null) {
            fechaFin = LocalDate.now();
        }

        // Si no es admin, solo puede ver sus propios reportes
        if (!esAdmin) {
            usuarioId = usuarioActual.getId();
        }

        ReporteDTO reporte = reporteService.generarReporte(usuarioId, fechaInicio, fechaFin);
        List<Usuario> usuarios = esAdmin ? usuarioService.obtenerTodosLosUsuarios() : List.of(usuarioActual);

        model.addAttribute("reporte", reporte);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("usuarioId", usuarioId);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        model.addAttribute("esAdmin", esAdmin);

        return "reportes";
    }

    @GetMapping("/dashboard/reportes")
    public String mostrarReportesAdmin(
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuarioActual = usuarioService.obtenerUsuarioPorEmail(auth.getName());
        
        // Verificar que el usuario sea admin
        if (usuarioActual.getRole() != Usuario.Role.ADMIN) {
            return "redirect:/reportes";
        }

        // Si no se especifican fechas, usar el mes actual
        if (fechaInicio == null) {
            fechaInicio = LocalDate.now().withDayOfMonth(1);
        }
        if (fechaFin == null) {
            fechaFin = LocalDate.now();
        }

        ReporteDTO reporte = reporteService.generarReporte(usuarioId, fechaInicio, fechaFin);
        List<Usuario> usuarios = usuarioService.obtenerTodosLosUsuarios();

        model.addAttribute("reporte", reporte);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("usuarioId", usuarioId);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        model.addAttribute("esAdmin", true);

        return "admin/reportes";
    }
}
