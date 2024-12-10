package com.control.acceso.application.service;

import com.control.acceso.domain.model.Usuario;
import com.control.acceso.domain.port.incoming.UsuarioManagementUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UsuarioManagementUseCase usuarioManagementUseCase;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setEmail("test@test.com");
        usuario.setPassword("hashedPassword");
        usuario.setRole(Usuario.Role.EMPLEADO);
    }

    @Test
    void loadUserByUsername_DeberiaRetornarUserDetails_CuandoUsuarioExiste() {
        when(usuarioManagementUseCase.obtenerUsuarioPorEmail("test@test.com")).thenReturn(usuario);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("test@test.com");

        assertNotNull(userDetails);
        assertEquals("test@test.com", userDetails.getUsername());
        assertEquals("hashedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_EMPLEADO")));
    }

    @Test
    void loadUserByUsername_DeberiaLanzarExcepcion_CuandoUsuarioNoExiste() {
        when(usuarioManagementUseCase.obtenerUsuarioPorEmail(anyString()))
                .thenThrow(new UsernameNotFoundException("Usuario no encontrado"));

        assertThrows(UsernameNotFoundException.class, () -> 
            customUserDetailsService.loadUserByUsername("noexiste@test.com")
        );
    }

    @Test
    void loadUserByUsername_DeberiaLanzarExcepcion_CuandoUsuarioEsNull() {
        when(usuarioManagementUseCase.obtenerUsuarioPorEmail(anyString())).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> 
            customUserDetailsService.loadUserByUsername("test@test.com")
        );
    }
}
