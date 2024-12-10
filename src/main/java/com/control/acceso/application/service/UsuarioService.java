package com.control.acceso.application.service;

import com.control.acceso.domain.model.Usuario;
import com.control.acceso.domain.port.incoming.UsuarioManagementUseCase;
import com.control.acceso.domain.port.outgoing.UsuarioRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService implements UsuarioManagementUseCase {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepositoryPort usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepositoryPort usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Usuario obtenerUsuarioPorEmail(String email) {
        logger.info("Buscando usuario por email: {}", email);
        try {
            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            logger.info("Usuario encontrado: email={}, role={}, password={}", 
                usuario.getEmail(), 
                usuario.getRole(),
                usuario.getPassword() != null ? "present" : "null");
            return usuario;
        } catch (Exception e) {
            logger.error("Error al buscar usuario por email: {}", email, e);
            throw e;
        }
    }

    @Override
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    @Override
    @Transactional
    public Usuario crearUsuario(Usuario usuario) {
        logger.info("Creando nuevo usuario: email={}, role={}", usuario.getEmail(), usuario.getRole());
        if (existeUsuarioPorEmail(usuario.getEmail())) {
            logger.error("Ya existe un usuario con el email: {}", usuario.getEmail());
            throw new RuntimeException("Ya existe un usuario con ese email");
        }
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        Usuario saved = usuarioRepository.save(usuario);
        logger.info("Usuario creado exitosamente: email={}, role={}", saved.getEmail(), saved.getRole());
        return saved;
    }

    @Override
    @Transactional
    public Usuario actualizarUsuario(Usuario usuario) {
        logger.info("Actualizando usuario: email={}, role={}", usuario.getEmail(), usuario.getRole());
        
        // Verificar si el usuario existe
        Usuario usuarioExistente = usuarioRepository.findByEmail(usuario.getEmail())
                .orElseThrow(() -> {
                    logger.error("Usuario no encontrado para actualizar: {}", usuario.getEmail());
                    return new RuntimeException("Usuario no encontrado");
                });
        
        // Si la contraseña es diferente, la codificamos
        if (usuario.getPassword() != null && !usuario.getPassword().equals(usuarioExistente.getPassword())) {
            String hashedPassword = passwordEncoder.encode(usuario.getPassword());
            logger.info("Actualizando contraseña para usuario: {}. Nueva contraseña hash: {}", 
                usuario.getEmail(), hashedPassword);
            usuario.setPassword(hashedPassword);
        } else {
            // Mantener la contraseña existente si no se proporciona una nueva
            usuario.setPassword(usuarioExistente.getPassword());
        }
        
        Usuario saved = usuarioRepository.save(usuario);
        logger.info("Usuario actualizado exitosamente: email={}, role={}", saved.getEmail(), saved.getRole());
        return saved;
    }

    @Override
    @Transactional
    public void eliminarUsuario(Long id) {
        logger.info("Eliminando usuario con id: {}", id);
        if (!usuarioRepository.findById(id).isPresent()) {
            logger.error("Usuario no encontrado con id: {}", id);
            throw new RuntimeException("Usuario no encontrado");
        }
        usuarioRepository.deleteById(id);
        logger.info("Usuario eliminado exitosamente con id: {}", id);
    }

    @Override
    public boolean existeUsuarioPorEmail(String email) {
        logger.info("Verificando existencia de usuario por email: {}", email);
        boolean exists = usuarioRepository.existsByEmail(email);
        logger.info("Usuario existe con email {}: {}", email, exists);
        return exists;
    }

    @Override
    public Usuario obtenerUsuarioPorId(Long id) {
        logger.info("Buscando usuario por id: {}", id);
        try {
            Usuario usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
            logger.info("Usuario encontrado: id={}, email={}, role={}, password={}", 
                usuario.getId(), 
                usuario.getEmail(), 
                usuario.getRole(),
                usuario.getPassword() != null ? "present" : "null");
            return usuario;
        } catch (Exception e) {
            logger.error("Error al buscar usuario por id: {}", id, e);
            throw e;
        }
    }
}
