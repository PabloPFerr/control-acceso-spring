package com.control.acceso.application.service;

import com.control.acceso.domain.model.Registro;
import com.control.acceso.domain.model.Usuario;
import com.control.acceso.interfaces.dto.ReporteDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

class ReporteServiceTest {

    @Mock
    private RegistroService registroService;

    @Mock
    private UsuarioService usuarioService;

    private ReporteService reporteService;

    private Usuario usuario;
    private List<Registro> registros;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reporteService = new ReporteService(registroService, usuarioService);
        
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Test User");
        usuario.setEmail("test@test.com");
        usuario.setRole(Usuario.Role.EMPLEADO);

        fechaInicio = LocalDate.now().minusDays(7);
        fechaFin = LocalDate.now();

        // Configurar el mock del usuarioService
        when(usuarioService.obtenerUsuarioPorId(usuario.getId())).thenReturn(usuario);

        // Crear algunos registros de prueba
        Registro registro1 = new Registro();
        registro1.setId(1L);
        registro1.setUsuario(usuario);
        registro1.setHoraEntrada(LocalDateTime.now().minusDays(1).withHour(9).withMinute(0));
        registro1.setHoraSalida(LocalDateTime.now().minusDays(1).withHour(17).withMinute(0));
        registro1.setTipoRegistro(Registro.TipoRegistro.MANUAL);

        Registro registro2 = new Registro();
        registro2.setId(2L);
        registro2.setUsuario(usuario);
        registro2.setHoraEntrada(LocalDateTime.now().withHour(9).withMinute(0));
        registro2.setHoraSalida(LocalDateTime.now().withHour(17).withMinute(0));
        registro2.setTipoRegistro(Registro.TipoRegistro.MANUAL);

        registros = Arrays.asList(registro1, registro2);
    }

    @Test
    void generarReporte_DebeCalcularHorasCorrectamente() {
        // Arrange
        when(registroService.obtenerRegistrosPorUsuarioYFechas(any(), any(), any()))
            .thenReturn(registros);

        // Act
        ReporteDTO reporte = reporteService.generarReporte(usuario.getId(), fechaInicio, fechaFin);

        // Assert
        assertNotNull(reporte);
        assertNotNull(reporte.getRegistrosDetallados());
        assertEquals(2, reporte.getRegistrosDetallados().size());
        
        // Verificar horas totales (8 horas por día)
        Double horasTotales = reporte.getHorasTotales().get(usuario.getNombre());
        assertNotNull(horasTotales);
        assertEquals(16.0, horasTotales);
    }

    @Test
    void generarReporte_SinRegistros_DebeRetornarReporteVacio() {
        // Arrange
        when(registroService.obtenerRegistrosPorUsuarioYFechas(any(), any(), any()))
            .thenReturn(List.of());

        // Act
        ReporteDTO reporte = reporteService.generarReporte(usuario.getId(), fechaInicio, fechaFin);

        // Assert
        assertNotNull(reporte);
        assertTrue(reporte.getRegistrosDetallados().isEmpty());
        assertTrue(reporte.getHorasTotales().isEmpty() || reporte.getHorasTotales().get(usuario.getNombre()) == 0.0);
    }

    @Test
    void generarReporte_ConRegistrosIncompletos_DebeCalcularCorrectamente() {
        // Arrange
        Registro registroIncompleto = new Registro();
        registroIncompleto.setId(3L);
        registroIncompleto.setUsuario(usuario);
        registroIncompleto.setHoraEntrada(LocalDateTime.now().withHour(9).withMinute(0));
        registroIncompleto.setTipoRegistro(Registro.TipoRegistro.MANUAL);
        // Sin hora de salida

        List<Registro> registrosConIncompleto = Arrays.asList(registros.get(0), registroIncompleto);
        
        when(registroService.obtenerRegistrosPorUsuarioYFechas(any(), any(), any()))
            .thenReturn(registrosConIncompleto);

        // Act
        ReporteDTO reporte = reporteService.generarReporte(usuario.getId(), fechaInicio, fechaFin);

        // Assert
        assertNotNull(reporte);
        assertEquals(2, reporte.getRegistrosDetallados().size());
        
        // Solo debe contar las horas del registro completo
        Double horasTotales = reporte.getHorasTotales().get(usuario.getNombre());
        assertNotNull(horasTotales);
        assertEquals(8.0, horasTotales);
    }
}
