package com.control.acceso.interfaces.api;

import com.control.acceso.application.service.ReporteService;
import com.control.acceso.application.service.UsuarioService;
import com.control.acceso.domain.model.Usuario;
import com.control.acceso.infrastructure.config.SecurityConfig;
import com.control.acceso.infrastructure.config.TestSecurityConfig;
import com.control.acceso.interfaces.dto.ReporteDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReporteApiController.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class ReporteApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReporteService reporteService;

    @MockBean
    private UsuarioService usuarioService;

    private Usuario usuarioAdmin;
    private Usuario usuarioNormal;
    private ReporteDTO reporteDTO;

    @BeforeEach
    void setUp() {
        usuarioAdmin = new Usuario();
        usuarioAdmin.setId(1L);
        usuarioAdmin.setEmail("admin@test.com");
        usuarioAdmin.setRole(Usuario.Role.ADMIN);

        usuarioNormal = new Usuario();
        usuarioNormal.setId(2L);
        usuarioNormal.setEmail("user@test.com");
        usuarioNormal.setRole(Usuario.Role.EMPLEADO);

        reporteDTO = new ReporteDTO();
        reporteDTO.setRegistrosDetallados(new ArrayList<>());
        reporteDTO.setHorasTotales(new HashMap<>());
        reporteDTO.setHorasPorDia(new HashMap<>());
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = {"EMPLEADO"})
    void obtenerReporte_UsuarioNormal_DebeRetornarSusReportes() throws Exception {
        when(usuarioService.obtenerUsuarioPorEmail("user@test.com")).thenReturn(usuarioNormal);
        when(reporteService.generarReporte(any(), any(), any())).thenReturn(reporteDTO);
        when(usuarioService.obtenerTodosLosUsuarios()).thenReturn(List.of(usuarioNormal));

        mockMvc.perform(get("/api/reportes")
                .param("fechaInicio", LocalDate.now().toString())
                .param("fechaFin", LocalDate.now().toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.registrosDetallados").exists())
                .andExpect(jsonPath("$.horasTotales").exists())
                .andExpect(jsonPath("$.horasPorDia").exists());
    }

    @Test
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void obtenerReporte_Admin_DebeRetornarReporteCompleto() throws Exception {
        when(usuarioService.obtenerUsuarioPorEmail("admin@test.com")).thenReturn(usuarioAdmin);
        when(reporteService.generarReporte(any(), any(), any())).thenReturn(reporteDTO);
        when(usuarioService.obtenerTodosLosUsuarios()).thenReturn(List.of(usuarioAdmin, usuarioNormal));

        mockMvc.perform(get("/api/admin/reportes")
                .param("fechaInicio", LocalDate.now().toString())
                .param("fechaFin", LocalDate.now().toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.registrosDetallados").exists())
                .andExpect(jsonPath("$.horasTotales").exists())
                .andExpect(jsonPath("$.horasPorDia").exists());
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = {"EMPLEADO"})
    void obtenerReporteAdmin_UsuarioNormal_DebeRetornar403() throws Exception {
        when(usuarioService.obtenerUsuarioPorEmail("user@test.com")).thenReturn(usuarioNormal);

        mockMvc.perform(get("/api/admin/reportes")
                .param("fechaInicio", LocalDate.now().toString())
                .param("fechaFin", LocalDate.now().toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void obtenerReporte_SinAutenticar_DebeRetornar401() throws Exception {
        mockMvc.perform(get("/api/reportes")
                .param("fechaInicio", LocalDate.now().toString())
                .param("fechaFin", LocalDate.now().toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
