package com.control.acceso.application.service;

import com.control.acceso.domain.model.Usuario;
import com.control.acceso.domain.port.outgoing.UsuarioRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("test@test.com");
        usuario.setPassword("password");
        usuario.setRole(Usuario.Role.EMPLEADO);
    }

    @Test
    void obtenerUsuarioPorEmail_DeberiaRetornarUsuario() {
        when(usuarioRepository.findByEmail("test@test.com")).thenReturn(Optional.of(usuario));

        Usuario result = usuarioService.obtenerUsuarioPorEmail("test@test.com");

        assertNotNull(result);
        assertEquals("test@test.com", result.getEmail());
        verify(usuarioRepository).findByEmail("test@test.com");
    }

    @Test
    void obtenerUsuarioPorEmail_DeberiaLanzarExcepcion_CuandoNoExisteUsuario() {
        when(usuarioRepository.findByEmail("noexiste@test.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> 
            usuarioService.obtenerUsuarioPorEmail("noexiste@test.com")
        );
    }

    @Test
    void obtenerTodosLosUsuarios_DeberiaRetornarListaDeUsuarios() {
        List<Usuario> usuarios = Arrays.asList(usuario);
        when(usuarioRepository.findAll()).thenReturn(usuarios);

        List<Usuario> result = usuarioService.obtenerTodosLosUsuarios();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(usuarioRepository).findAll();
    }

    @Test
    void crearUsuario_DeberiaCrearUsuarioExitosamente() {
        when(usuarioRepository.save(any())).thenReturn(usuario);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");

        Usuario result = usuarioService.crearUsuario(usuario);

        assertNotNull(result);
        assertEquals("test@test.com", result.getEmail());
        verify(passwordEncoder).encode("password");
        verify(usuarioRepository).save(any());
    }

    @Test
    void crearUsuario_DeberiaLanzarExcepcion_CuandoEmailExiste() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> 
            usuarioService.crearUsuario(usuario)
        );
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void actualizarUsuario_DeberiaActualizarUsuarioExitosamente() {
        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setId(1L);
        usuarioExistente.setEmail("test@test.com");
        usuarioExistente.setPassword("oldPassword");

        when(usuarioRepository.findByEmail(any())).thenReturn(Optional.of(usuarioExistente));
        when(passwordEncoder.encode(any())).thenReturn("newHashedPassword");
        when(usuarioRepository.save(any())).thenReturn(usuario);

        Usuario result = usuarioService.actualizarUsuario(usuario);

        assertNotNull(result);
        assertEquals("test@test.com", result.getEmail());
        verify(passwordEncoder).encode("password");
        verify(usuarioRepository).save(any());
    }

    @Test
    void eliminarUsuario_DeberiaEliminarUsuarioExitosamente() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        usuarioService.eliminarUsuario(1L);

        verify(usuarioRepository).deleteById(1L);
    }

    @Test
    void eliminarUsuario_DeberiaLanzarExcepcion_CuandoNoExisteUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> 
            usuarioService.eliminarUsuario(1L)
        );
        verify(usuarioRepository, never()).deleteById(any());
    }
}
