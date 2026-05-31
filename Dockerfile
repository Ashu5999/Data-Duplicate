# ============================================================
# Dockerfile  —  DDAS Backend (Multi-stage Build)
# ============================================================
# Placed at project root. Builds from the ddas-backend/ subfolder.
# Stage 1: Build the application JAR using Maven
# Stage 2: Run the JAR in a lightweight JRE container
# ============================================================

# ── Stage 1: Build ───────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy Maven wrapper and config first (for Docker layer caching)
COPY ddas-backend/pom.xml .
COPY ddas-backend/.mvn .mvn
COPY ddas-backend/mvnw .
RUN chmod +x mvnw

# Download dependencies (cached if pom.xml hasn't changed)
RUN ./mvnw dependency:go-offline -B

# Copy source code and build
COPY ddas-backend/src src
RUN ./mvnw package -DskipTests -B

# ── Stage 2: Run ─────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the built JAR from Stage 1
COPY --from=build /app/target/*.jar app.jar

# Create data directory for any local storage needs
RUN mkdir -p /app/data

# Expose the port (Render uses $PORT env variable)
EXPOSE 8080

# Run with production profile
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
