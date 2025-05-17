# --- STAGE 1: Build the application ---
    
    FROM eclipse-temurin:17-jdk-alpine
    RUN apk add --no-cache postgresql-client
    
    FROM maven:3.8.3-openjdk-17-slim AS builder
    WORKDIR /app
    
    # Copy just what's needed for Maven to work
    COPY pom.xml .
    COPY document-service/pom.xml document-service/pom.xml
    COPY document-service/src document-service/src
    
    # Build the application (this creates the Spring Boot fat jar)
    WORKDIR /app/document-service
    RUN mvn clean package -DskipTests
    
    # --- STAGE 2: Run the application ---
    FROM eclipse-temurin:17-jre-alpine
    WORKDIR /app
    
    # Copy the Spring Boot fat jar (notice the different path)
    COPY --from=builder /app/document-service/target/document-service-*.jar app.jar
    
    ENTRYPOINT ["java", "-jar", "app.jar"]