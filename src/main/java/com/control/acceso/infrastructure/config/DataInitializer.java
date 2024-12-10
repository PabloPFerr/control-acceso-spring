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
            
            String adminEmail = "admin@example.com";
            String adminPassword = "admin123";
            
            try {
                if (!usuarioManagementUseCase.existeUsuarioPorEmail(adminEmail)) {
                    logger.info("No admin user found. Creating default admin user.");
                    Usuario admin = new Usuario();
                    admin.setNombre("Admin");
                    admin.setEmail(adminEmail);
                    String hashedPassword = passwordEncoder.encode(adminPassword);
                    logger.info("Created hashed password for admin: {}", hashedPassword);
                    admin.setPassword(hashedPassword);
                    admin.setRole(Usuario.Role.ADMIN);
                    usuarioManagementUseCase.crearUsuario(admin);
                    logger.info("Default admin user created successfully with email: {} and role: {}", 
                        admin.getEmail(), admin.getRole());
                } else {
                    // Si el usuario existe, actualizamos su contraseña
                    logger.info("Admin user exists. Updating password if necessary...");
                    Usuario admin = usuarioManagementUseCase.obtenerUsuarioPorEmail(adminEmail);
                    
                    // Solo actualizamos si la contraseña ha cambiado
                    if (!passwordEncoder.matches(adminPassword, admin.getPassword())) {
                        String hashedPassword = passwordEncoder.encode(adminPassword);
                        logger.info("Updating admin password. New hash: {}", hashedPassword);
                        admin.setPassword(hashedPassword);
                        usuarioManagementUseCase.actualizarUsuario(admin);
                        logger.info("Admin password updated successfully");
                    } else {
                        logger.info("Admin password is already up to date");
                    }
                }
            } catch (Exception e) {
                logger.error("Error during database initialization", e);
                throw e;
            }
            
            logger.info("Database initialization complete.");
        };
    }
}
