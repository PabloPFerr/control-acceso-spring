package com.control.acceso.application.service;

import com.control.acceso.domain.model.Registro;
import com.control.acceso.domain.model.Usuario;
import com.control.acceso.interfaces.dto.ReporteDTO;
import com.control.acceso.interfaces.dto.RegistroDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ReporteService {

    private static final Logger logger = LoggerFactory.getLogger(ReporteService.class);
    private final RegistroService registroService;
    private final UsuarioService usuarioService;

    public ReporteService(RegistroService registroService, UsuarioService usuarioService) {
        this.registroService = registroService;
        this.usuarioService = usuarioService;
    }

    @Transactional(readOnly = true)
    public ReporteDTO generarReporte(Long usuarioId, LocalDate fechaInicio, LocalDate fechaFin) {
        logger.info("Generando reporte para usuario={}, fechaInicio={}, fechaFin={}", 
            usuarioId, fechaInicio, fechaFin);

        List<Registro> registros;
        if (usuarioId != null) {
            registros = registroService.obtenerRegistrosPorUsuarioYFechas(usuarioId, fechaInicio, fechaFin);
        } else {
            registros = registroService.obtenerRegistrosPorFechas(fechaInicio, fechaFin);
        }

        ReporteDTO reporte = new ReporteDTO();
        reporte.setRegistrosDetallados(convertirARegistrosDTO(registros));
        reporte.setHorasPorDia(calcularHorasPorDia(registros));
        reporte.setHorasTotales(calcularHorasTotales(registros));

        // Preparar datos para gráficos
        Map<String, List<Double>> datosGrafico = new LinkedHashMap<>();
        List<LocalDate> fechas = new ArrayList<>();
        
        // Generar lista de fechas
        LocalDate fecha = fechaInicio;
        while (!fecha.isAfter(fechaFin)) {
            fechas.add(fecha);
            fecha = fecha.plusDays(1);
        }

        // Obtener todos los usuarios relevantes
        Set<Usuario> usuarios;
        if (usuarioId != null) {
            usuarios = Collections.singleton(usuarioService.obtenerUsuarioPorId(usuarioId));
        } else {
            usuarios = new HashSet<>(usuarioService.obtenerTodosLosUsuarios());
        }

        // Generar datos para cada usuario
        for (Usuario usuario : usuarios) {
            List<Double> horasPorDia = new ArrayList<>();
            
            for (LocalDate currentDate : fechas) {
                double horas = registros.stream()
                    .filter(r -> r.getUsuario().getId().equals(usuario.getId()) &&
                            r.getHoraEntrada().toLocalDate().equals(currentDate) &&
                            r.getHoraSalida() != null)
                    .mapToDouble(r -> ChronoUnit.MINUTES.between(r.getHoraEntrada(), r.getHoraSalida()) / 60.0)
                    .sum();
                horasPorDia.add(Math.round(horas * 100.0) / 100.0); // Redondear a 2 decimales
            }
            
            datosGrafico.put(usuario.getNombre(), horasPorDia);
            logger.debug("Datos generados para usuario {}: {}", usuario.getNombre(), horasPorDia);
        }

        reporte.setDatosGraficoLineas(datosGrafico);
        reporte.setFechasGrafico(fechas);

        logger.info("Reporte generado exitosamente con {} registros", registros.size());
        return reporte;
    }

    private List<RegistroDTO> convertirARegistrosDTO(List<Registro> registros) {
        logger.debug("Convirtiendo registros a DTO. Cantidad de registros: {}", registros.size());
        try {
            return registros.stream()
                .map(r -> {
                    try {
                        logger.debug("Procesando registro ID: {}", r.getId());
                        logger.debug("Usuario: {}", r.getUsuario().getNombre());
                        logger.debug("Hora entrada: {}", r.getHoraEntrada());
                        logger.debug("Hora salida: {}", r.getHoraSalida());
                        logger.debug("Tipo registro: {}", r.getTipoRegistro());
                        
                        RegistroDTO dto = new RegistroDTO();
                        dto.setId(r.getId());
                        dto.setUsuarioId(r.getUsuario().getId());
                        dto.setNombreUsuario(r.getUsuario().getNombre());
                        dto.setHoraEntrada(r.getHoraEntrada());
                        dto.setHoraSalida(r.getHoraSalida());
                        dto.setTipoRegistro(r.getTipoRegistro().toString());
                        if (r.getHoraSalida() != null) {
                            double horas = ChronoUnit.MINUTES.between(r.getHoraEntrada(), r.getHoraSalida()) / 60.0;
                            dto.setDuracion(Math.round(horas * 100.0) / 100.0);
                        }
                        return dto;
                    } catch (Exception e) {
                        logger.error("Error procesando registro {}: {}", r.getId(), e.getMessage(), e);
                        throw e;
                    }
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error general en convertirARegistrosDTO: {}", e.getMessage(), e);
            throw e;
        }
    }

    private Map<LocalDate, Map<String, Double>> calcularHorasPorDia(List<Registro> registros) {
        return registros.stream()
            .filter(r -> r.getHoraSalida() != null)
            .collect(Collectors.groupingBy(
                r -> r.getHoraEntrada().toLocalDate(),
                Collectors.groupingBy(
                    r -> r.getUsuario().getNombre(),
                    Collectors.collectingAndThen(
                        Collectors.summingDouble(r -> 
                            ChronoUnit.MINUTES.between(r.getHoraEntrada(), r.getHoraSalida()) / 60.0
                        ),
                        horas -> Math.round(horas * 100.0) / 100.0 // Redondear a 2 decimales
                    )
                )
            ));
    }

    private Map<String, Double> calcularHorasTotales(List<Registro> registros) {
        return registros.stream()
            .filter(r -> r.getHoraSalida() != null)
            .collect(Collectors.groupingBy(
                r -> r.getUsuario().getNombre(),
                Collectors.collectingAndThen(
                    Collectors.summingDouble(r -> 
                        ChronoUnit.MINUTES.between(r.getHoraEntrada(), r.getHoraSalida()) / 60.0
                    ),
                    horas -> Math.round(horas * 100.0) / 100.0 // Redondear a 2 decimales
                )
            ));
    }
}
