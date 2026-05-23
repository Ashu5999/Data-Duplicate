package com.ddas.backend;

// ============================================================
// DdasBackendApplication.java  —  Package: com.ddas.backend
// ------------------------------------------------------------
// PURPOSE:
//   This is the ENTRY POINT of the entire Spring Boot application.
//   Running main() starts the embedded Tomcat web server and
//   bootstraps all Spring beans (controllers, services, repos).
//
// @SpringBootApplication does three things in one annotation:
//   1. @Configuration       → This class can define @Bean methods
//   2. @EnableAutoConfiguration → Auto-configures Spring based on
//                                 dependencies found on the classpath
//                                 (e.g. H2, JPA, Spring Security, Web MVC)
//   3. @ComponentScan       → Scans this package and all sub-packages
//                             for @Component, @Service, @Repository,
//                             @Controller, @RestController, @Configuration
//
// PACKAGE STRUCTURE OVERVIEW:
//   com.ddas.backend
//   ├── DdasBackendApplication.java   ← YOU ARE HERE (app entry point)
//   │
//   ├── config/
//   │   └── SecurityConfig.java       ← Spring Security + CORS setup
//   │
//   ├── controller/
//   │   ├── AuthController.java       ← HTTP routes for /api/auth/*
//   │   └── FileController.java       ← HTTP routes for /api/files/*
//   │
//   ├── service/
//   │   ├── AuthService.java          ← Business logic: login, register, reset
//   │   └── FileService.java          ← Business logic: upload, download, delete
//   │
//   ├── model/
//   │   ├── User.java                 ← JPA entity → "users" table
//   │   └── FileData.java             ← JPA entity → "file_data" table
//   │
//   └── repository/
//       ├── UserRepository.java       ← DB queries for users
//       └── FileRepository.java       ← DB queries for files
//
// HOW TO RUN:
//   IntelliJ IDEA: Click the green ▶ button on this file
//   Terminal:      cd ddas-backend && ./mvnw spring-boot:run
//   Access at:     http://localhost:8080
//   H2 Console:    http://localhost:8080/h2-console
// ============================================================

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DdasBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(DdasBackendApplication.class, args);
    }
}
