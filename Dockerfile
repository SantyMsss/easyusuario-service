# ============================================
# Multi-stage Dockerfile para Spring Boot
# Optimizado para Railway deployment
# ============================================

# ============================================
# STAGE 1: Build
# ============================================
FROM maven:3.9.5-eclipse-temurin-17-alpine AS builder

# Metadata
LABEL maintainer="SantyMsss"
LABEL description="EasySave Usuario Service - Spring Boot Application"

# Establecer directorio de trabajo
WORKDIR /app

# Copiar archivos de configuración de Maven
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Descargar dependencias (se cachean si pom.xml no cambia)
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Compilar aplicación (skip tests para build más rápido)
RUN mvn clean package -DskipTests

# ============================================
# STAGE 2: Runtime
# ============================================
FROM eclipse-temurin:17-jre-alpine

# Metadata
LABEL version="1.0.0"
LABEL description="EasySave Usuario Service - Production Runtime"

# Instalar dependencias del sistema
RUN apk add --no-cache \
    curl \
    bash \
    tzdata

# Configurar timezone
ENV TZ=America/Bogota
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Crear usuario no-root para seguridad
RUN addgroup -g 1001 -S spring && \
    adduser -u 1001 -S spring -G spring

# Crear directorio de aplicación
WORKDIR /app

# Copiar JAR desde stage de build
COPY --from=builder /app/target/celular-service-0.0.1-SNAPSHOT.jar app.jar

# Cambiar ownership al usuario spring
RUN chown -R spring:spring /app

# Cambiar a usuario no-root
USER spring:spring

# Exponer puerto (Railway lo sobrescribe con $PORT)
EXPOSE 8080

# Variables de entorno por defecto
ENV SPRING_PROFILES_ACTIVE=railway \
    JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Comando de inicio
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]
