package com.control.acceso.application.service;

import com.control.acceso.domain.model.Usuario;
import com.control.acceso.domain.port.incoming.UsuarioManagementUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UsuarioManagementUseCase usuarioManagementUseCase;

    @Autowired
    public CustomUserDetailsService(UsuarioManagementUseCase usuarioManagementUseCase) {
        this.usuarioManagementUseCase = usuarioManagementUseCase;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            logger.info("Attempting to load user by email: {}", email);
            
            Usuario usuario = usuarioManagementUseCase.obtenerUsuarioPorEmail(email);
            if (usuario == null) {
                logger.error("User not found with email: {}", email);
                throw new UsernameNotFoundException("User not found with email: " + email);
            }

            logger.info("User found: email={}, role={}, password={}", 
                usuario.getEmail(), 
                usuario.getRole(),
                usuario.getPassword() != null ? "present" : "null");
            
            UserDetails userDetails = User.builder()
                    .username(usuario.getEmail())
                    .password(usuario.getPassword())
                    .roles(usuario.getRole().name())
                    .build();
                    
            logger.info("UserDetails created successfully for user: {}, with roles: {}, password hash: {}", 
                email, 
                userDetails.getAuthorities(),
                userDetails.getPassword());
            
            return userDetails;
        } catch (Exception e) {
            logger.error("Error loading user by email: {}", email, e);
            throw e;
        }
    }
}
