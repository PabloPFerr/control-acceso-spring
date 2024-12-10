package com.control.acceso.interfaces.api;

import com.control.acceso.application.service.ReporteService;
import com.control.acceso.application.service.UsuarioService;
import com.control.acceso.domain.model.Usuario;
import com.control.acceso.interfaces.dto.ReporteDTO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/api")
public class ReporteApiController {

    private final ReporteService reporteService;
    private final UsuarioService usuarioService;

    public ReporteApiController(ReporteService reporteService, UsuarioService usuarioService) {
        this.reporteService = reporteService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/reportes")
    public ResponseEntity<ReporteDTO> obtenerReporte(
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaFin) {

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
        return ResponseEntity.ok(reporte);
    }

    @GetMapping("/admin/reportes")
    public ResponseEntity<ReporteDTO> obtenerReporteAdmin(
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaFin) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuarioActual = usuarioService.obtenerUsuarioPorEmail(auth.getName());
        
        // Verificar que el usuario sea admin
        if (usuarioActual.getRole() != Usuario.Role.ADMIN) {
            return ResponseEntity.status(403).build();
        }

        // Si no se especifican fechas, usar el mes actual
        if (fechaInicio == null) {
            fechaInicio = LocalDate.now().withDayOfMonth(1);
        }
        if (fechaFin == null) {
            fechaFin = LocalDate.now();
        }

        ReporteDTO reporte = reporteService.generarReporte(usuarioId, fechaInicio, fechaFin);
        return ResponseEntity.ok(reporte);
    }
}
