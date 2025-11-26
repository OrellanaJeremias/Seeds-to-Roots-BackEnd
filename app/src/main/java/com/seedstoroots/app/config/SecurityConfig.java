package com.seedstoroots.app.config;

import com.seedstoroots.app.security.filters.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas (sin autenticación)
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        
                        // Rutas de productos (lectura pública, escritura solo ADMIN)
                        .requestMatchers("GET", "/api/productos/**").permitAll()
                        .requestMatchers("POST", "/api/productos/**").hasRole("ADMIN")
                        .requestMatchers("PUT", "/api/productos/**").hasRole("ADMIN")
                        .requestMatchers("DELETE", "/api/productos/**").hasRole("ADMIN")
                        
                        // Rutas de usuarios (solo ADMIN)
                        .requestMatchers("/api/usuarios/**").hasRole("ADMIN")
                        
                        // Rutas de carrito (solo usuarios autenticados)
                        .requestMatchers("/api/carrito/**").authenticated()
                        
                        // Rutas de estadísticas (solo ADMIN)
                        .requestMatchers("/api/estadisticas/**").hasRole("ADMIN")
                        
                        // Cualquier otra ruta requiere autenticación
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}