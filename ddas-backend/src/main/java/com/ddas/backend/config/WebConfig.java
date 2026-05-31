package com.ddas.backend.config;

// ============================================================
// WebConfig.java  —  Package: com.ddas.backend.config
// ------------------------------------------------------------
// PURPOSE:
//   Redirects the root URL "/" to the login page (Index.html).
//   When deployed, visiting https://ddas.onrender.com will
//   automatically open the login page.
// ============================================================

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Redirect root URL to the login page
        registry.addRedirectViewController("/", "/Index.html");
    }
}
