# Multi-stage Dockerfile for Account Management Application
# Build stage
FROM gradle:8.10-jdk21-alpine AS build

WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradle gradle
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .

# Copy source code
COPY src src

# Build the application
RUN chmod +x gradlew && \
    ./gradlew clean build -x test --no-daemon

# Extract the JAR file
RUN mkdir -p /app/extracted && \
    java -Djarmode=layertools -jar build/libs/*.jar extract --destination /app/extracted

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

# Install necessary packages
RUN apk add --no-cache \
    tzdata \
    wget \
    && rm -rf /var/cache/apk/*

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

# Copy extracted layers from build stage
COPY --from=build /app/extracted/dependencies/ ./
COPY --from=build /app/extracted/spring-boot-loader/ ./
COPY --from=build /app/extracted/snapshot-dependencies/ ./
COPY --from=build /app/extracted/application/ ./

# Create logs directory
RUN mkdir -p /app/logs && \
    chown -R spring:spring /app

# Switch to non-root user
USER spring:spring

# Expose application port
EXPOSE 8080

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-Xmx512m -Xms256m" \
    TZ=UTC

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]

