# ============================================================
# Dockerfile  —  DDAS Backend (Multi-stage Build)
# ============================================================
# Stage 1: Build the application JAR using Maven
# Stage 2: Run the JAR in a lightweight JRE container
# ============================================================

# ── Stage 1: Build ───────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy Maven wrapper and config first (for Docker layer caching)
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw

# Download dependencies (cached if pom.xml hasn't changed)
RUN ./mvnw dependency:go-offline -B

# Copy source code and build
COPY src src
RUN ./mvnw package -DskipTests -B

# ── Stage 2: Run ─────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the built JAR from Stage 1
COPY --from=build /app/target/*.jar app.jar

# Create data directory for H2 database persistence
RUN mkdir -p /app/data

# Expose the port (Render uses $PORT env variable)
EXPOSE 8080

# Run with production profile
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
