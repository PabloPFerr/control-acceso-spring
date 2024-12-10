package com.control.acceso.interfaces.web;

import com.control.acceso.domain.model.Usuario;
import com.control.acceso.domain.port.incoming.RegistroManagementUseCase;
import com.control.acceso.domain.port.incoming.UsuarioManagementUseCase;
import com.control.acceso.infrastructure.config.SecurityConfig;
import com.control.acceso.infrastructure.config.TestSecurityConfig;
import com.control.acceso.interfaces.dto.RegistroManualRequestDTO;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegistroController.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class RegistroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegistroManagementUseCase registroManagementUseCase;

    @MockBean
    private UsuarioManagementUseCase usuarioManagementUseCase;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("test@test.com");
        usuario.setRole(Usuario.Role.EMPLEADO);
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void registrarEntrada_DeberiaRedirigirADashboard() throws Exception {
        when(usuarioManagementUseCase.obtenerUsuarioPorEmail("test@test.com")).thenReturn(usuario);

        mockMvc.perform(post("/registro/entrada"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));

        verify(usuarioManagementUseCase).obtenerUsuarioPorEmail("test@test.com");
        verify(registroManagementUseCase).registrarEntrada(usuario);
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void registrarSalida_DeberiaRedirigirADashboard() throws Exception {
        when(usuarioManagementUseCase.obtenerUsuarioPorEmail("test@test.com")).thenReturn(usuario);

        mockMvc.perform(post("/registro/salida"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));

        verify(usuarioManagementUseCase).obtenerUsuarioPorEmail("test@test.com");
        verify(registroManagementUseCase).registrarSalida(usuario);
    }

    @Test
    @WithMockUser(username = "test@test.com", roles = "ADMIN")
    void mostrarFormularioManual_DeberiaRetornarVistaConUsuarios() throws Exception {
        List<Usuario> usuarios = Arrays.asList(usuario);
        when(usuarioManagementUseCase.obtenerTodosLosUsuarios()).thenReturn(usuarios);

        mockMvc.perform(get("/registro/manual"))
                .andExpect(status().isOk())
                .andExpect(view().name("registro/manual"))
                .andExpect(model().attributeExists("usuarios"));

        verify(usuarioManagementUseCase).obtenerTodosLosUsuarios();
    }

    @Test
    @WithMockUser(username = "test@test.com", roles = "ADMIN")
    void registrarManual_DeberiaRedirigirAAdminReportes() throws Exception {
        when(usuarioManagementUseCase.obtenerUsuarioPorId(1L)).thenReturn(usuario);
        
        RegistroManualRequestDTO requestDTO = new RegistroManualRequestDTO();
        requestDTO.setUsuarioId(1L);
        requestDTO.setEntrada(LocalDateTime.now());
        requestDTO.setSalida(LocalDateTime.now());

        mockMvc.perform(post("/registro/manual")
                .flashAttr("registroManualRequestDTO", requestDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/reportes"));

        verify(registroManagementUseCase).registrarManualEditado(any(Usuario.class), any(LocalDateTime.class), any(LocalDateTime.class));
    }
}
