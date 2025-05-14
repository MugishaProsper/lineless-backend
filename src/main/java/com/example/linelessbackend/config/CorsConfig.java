package com.example.linelessbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Allow frontend origins
        config.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000", // React default
            "http://127.0.0.1:3000",
            "http://localhost:5173", // Vite's default ports
            "http://127.0.0.1:5173",
            "http://localhost:8000", // Same as backend port
            "http://127.0.0.1:8000",
            "http://localhost:8080", // Alternative port
            "http://127.0.0.1:8080"
        ));
        
        // Allow credentials
        config.setAllowCredentials(true);
        
        // Allow all common HTTP methods
        config.addAllowedMethod("*");
        
        // Allow all headers
        config.addAllowedHeader("*");
        
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}