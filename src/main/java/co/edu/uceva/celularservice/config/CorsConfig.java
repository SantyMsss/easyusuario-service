package co.edu.uceva.celularservice.config;

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
        
        // Permitir credenciales
        config.setAllowCredentials(true);
        
        // Permitir todos los orígenes (para desarrollo)
        // En producción, especifica los dominios permitidos: config.setAllowedOrigins(Arrays.asList("http://localhost:3000", "https://tu-dominio.com"));
        config.addAllowedOriginPattern("*");
        
        // Permitir todos los headers
        config.addAllowedHeader("*");
        
        // Permitir todos los métodos HTTP
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        
        // Tiempo de cache para la respuesta preflight (en segundos)
        config.setMaxAge(3600L);
        
        // Aplicar configuración a todas las rutas
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}
