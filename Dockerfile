FROM gradle:8.10-jdk21-alpine AS build

WORKDIR /app

COPY gradle gradle
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .

RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

COPY src src

RUN chmod +x gradlew && \
    ./gradlew build -x test --no-daemon

RUN mkdir -p /app/extracted && \
    java -Djarmode=layertools -jar $(find build/libs/ -name "*.jar" ! -name "*-plain.jar") extract --destination /app/extracted

FROM eclipse-temurin:21-jre-alpine

RUN apk add --no-cache \
    tzdata \
    curl \
    && rm -rf /var/cache/apk/*

RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

COPY --from=build /app/extracted/dependencies/ ./
COPY --from=build /app/extracted/spring-boot-loader/ ./
COPY --from=build /app/extracted/snapshot-dependencies/ ./
COPY --from=build /app/extracted/application/ ./

RUN mkdir -p /app/logs && \
    chown -R spring:spring /app

USER spring:spring

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-Xms512m -Xmx512m" \
    TZ=UTC

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]