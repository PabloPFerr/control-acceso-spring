package com.control.acceso.interfaces.web;

import com.control.acceso.domain.model.Registro;
import com.control.acceso.domain.model.Usuario;
import com.control.acceso.domain.port.incoming.RegistroManagementUseCase;
import com.control.acceso.domain.port.incoming.UsuarioManagementUseCase;
import com.control.acceso.infrastructure.config.SecurityConfig;
import com.control.acceso.infrastructure.config.TestSecurityConfig;
import com.control.acceso.interfaces.dto.RegistroDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegistroManagementUseCase registroManagementUseCase;

    @MockBean
    private UsuarioManagementUseCase usuarioManagementUseCase;

    private Usuario usuario;
    private Registro registro;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("test@test.com");
        usuario.setRole(Usuario.Role.EMPLEADO);

        registro = new Registro();
        registro.setId(1L);
        registro.setUsuario(usuario);
        registro.setHoraEntrada(LocalDateTime.now());
        registro.setTipoRegistro(Registro.TipoRegistro.MANUAL);
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void dashboard_DeberiaRetornarVistaDashboard() throws Exception {
        List<Registro> registros = Arrays.asList(registro);
        when(usuarioManagementUseCase.obtenerUsuarioPorEmail(any())).thenReturn(usuario);
        when(registroManagementUseCase.obtenerRegistrosUsuario(any())).thenReturn(registros);
        when(registroManagementUseCase.tieneRegistroAbierto(any())).thenReturn(false);

        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attributeExists("usuario", "registros", "tieneRegistroAbierto"));
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void registrarEntrada_DeberiaRedirigirADashboard() throws Exception {
        when(usuarioManagementUseCase.obtenerUsuarioPorEmail(any())).thenReturn(usuario);

        mockMvc.perform(post("/dashboard/entrada"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"))
                .andExpect(flash().attributeExists("mensaje"));

        verify(registroManagementUseCase).registrarEntrada(any(Usuario.class));
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void registrarSalida_DeberiaRedirigirADashboard() throws Exception {
        when(usuarioManagementUseCase.obtenerUsuarioPorEmail(any())).thenReturn(usuario);

        mockMvc.perform(post("/dashboard/salida"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"))
                .andExpect(flash().attributeExists("mensaje"));

        verify(registroManagementUseCase).registrarSalida(any(Usuario.class));
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void registrarEntrada_DeberiaRedirigirConError_CuandoOcurreExcepcion() throws Exception {
        when(usuarioManagementUseCase.obtenerUsuarioPorEmail(any())).thenReturn(usuario);
        doThrow(new RuntimeException("Error al registrar entrada")).when(registroManagementUseCase).registrarEntrada(any());

        mockMvc.perform(post("/dashboard/entrada"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"))
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void registrarSalida_DeberiaRedirigirConError_CuandoOcurreExcepcion() throws Exception {
        when(usuarioManagementUseCase.obtenerUsuarioPorEmail(any())).thenReturn(usuario);
        doThrow(new RuntimeException("Error al registrar salida")).when(registroManagementUseCase).registrarSalida(any());

        mockMvc.perform(post("/dashboard/salida"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"))
                .andExpect(flash().attributeExists("error"));
    }
}
