package com.example.linelessbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(
                    "http://localhost:3000", // React default
                    "http://127.0.0.1:3000",
                    "http://localhost:5173", // Vite's default ports
                    "http://127.0.0.1:5173",
                    "http://localhost:8000", // Same as backend port
                    "http://127.0.0.1:8000",
                    "http://localhost:8080", // Alternative port
                    "http://127.0.0.1:8080"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
} 