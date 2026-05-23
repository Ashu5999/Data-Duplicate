package com.ddas.backend.config;

// ============================================================
// SecurityConfig.java  —  Package: com.ddas.backend.config
// ------------------------------------------------------------
// PURPOSE:
//   Configures Spring Security for the DDAS application.
//   - Disables CSRF (not needed for stateless REST APIs)
//   - Permits ALL HTTP requests without authentication
//     (login/register is handled manually in AuthController)
//   - Enables the H2 console by relaxing frame-options
//   - Sets up CORS so the HTML frontend (on any origin)
//     can call the backend at http://localhost:8080
// ============================================================

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration          // Marks this class as a Spring configuration source
@EnableWebSecurity      // Activates Spring Security's web support
public class SecurityConfig {

    /**
     * Main security filter chain bean.
     * Every HTTP request passes through this chain.
     *
     * Rules defined here:
     *  - CSRF disabled   → Safe for REST APIs (no browser-session state)
     *  - All routes open → Authentication is done via custom logic in AuthController
     *  - H2 console      → Needs frames allowed (frameOptions disabled)
     *  - CORS            → Delegates to corsConfigurationSource() bean below
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF protection (not needed for a stateless REST API)
            .csrf(csrf -> csrf.disable())

            // Allow all requests without Spring Security authentication
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )

            // Allow H2 console to be shown inside an <iframe>
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.disable())
            )

            // Apply the CORS configuration defined below
            .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        return http.build();
    }

    /**
     * CORS configuration bean.
     *
     * Allows the static HTML frontend (opened directly from the filesystem
     * or any localhost port) to make fetch() calls to this Spring Boot server.
     *
     * Settings:
     *  - allowedOriginPatterns("*") → accept requests from any origin
     *  - allowedMethods             → GET, POST, PUT, DELETE, OPTIONS
     *  - allowedHeaders("*")        → accept any request headers
     *  - allowCredentials(true)     → allow cookies / auth headers
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Accept requests from any origin (e.g., file://, localhost:5500, etc.)
        config.setAllowedOriginPatterns(List.of("*"));

        // Allow common HTTP verbs used by the frontend
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Allow all request headers
        config.setAllowedHeaders(List.of("*"));

        // Allow the browser to send credentials (cookies / Authorization header)
        config.setAllowCredentials(true);

        // Apply this configuration to every endpoint ("/**")
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
