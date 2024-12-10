package com.control.acceso.infrastructure.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails adminUser = User.builder()
                .username("admin@test.com")
                .password(passwordEncoder().encode("password"))
                .roles("ADMIN")
                .build();

        UserDetails normalUser = User.builder()
                .username("user@test.com")
                .password(passwordEncoder().encode("password"))
                .roles("EMPLEADO")
                .build();

        return new InMemoryUserDetailsManager(adminUser, normalUser);
    }
}
