package com.control.acceso.infrastructure.config;

import com.control.acceso.domain.model.Usuario;
import com.control.acceso.domain.port.incoming.UsuarioManagementUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner initDatabase(UsuarioManagementUseCase usuarioManagementUseCase, PasswordEncoder passwordEncoder) {
        return args -> {
            logger.info("Initializing database...");
            if (!usuarioManagementUseCase.existeUsuarioPorEmail("admin@example.com")) {
                logger.info("No admin user found. Creating default admin user.");
                Usuario admin = new Usuario();
                admin.setNombre("Admin");
                admin.setEmail("admin@example.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(Usuario.Role.ADMIN);
                usuarioManagementUseCase.crearUsuario(admin);
                logger.info("Default admin user created successfully with email: {} and role: {}", 
                    admin.getEmail(), admin.getRole());
                
                // Verificar que el usuario se guardó correctamente
                Usuario saved = usuarioManagementUseCase.obtenerUsuarioPorEmail("admin@example.com");
                if (saved != null) {
                    logger.info("Verified: Admin user exists in database with email: {} and role: {}", 
                        saved.getEmail(), saved.getRole());
                }
            } else {
                logger.info("Admin user already exists. Current admin details:");
                Usuario existing = usuarioManagementUseCase.obtenerUsuarioPorEmail("admin@example.com");
                if (existing != null) {
                    logger.info("Email: {}, Role: {}", existing.getEmail(), existing.getRole());
                }
            }
            logger.info("Database initialization complete.");
        };
    }
}
