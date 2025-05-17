    FROM eclipse-temurin:17-jdk-alpine
    RUN apk add --no-cache postgresql-client


# --- STAGE 1: install parent POM into local Maven repo ---
    FROM maven:3.8.3-openjdk-17-slim AS parent-install
    WORKDIR /app
    
    # copy only the root pom.xml (your enterprise-dms parent POM)
    COPY pom.xml ./
    
    # install parent (no modules built)
    RUN mvn install -N -B
    
    # --- STAGE 2: build the auth-service module ---
    FROM maven:3.8.3-openjdk-17-slim AS builder
    WORKDIR /app
    
    # bring in the Maven cache (with parent already installed)
    COPY --from=parent-install /root/.m2 /root/.m2
    
    # copy the auth-service POM+sources
    COPY auth-service/pom.xml ./pom.xml
    COPY auth-service/src ./src
    
    # build and repackage into a runnable fat-JAR
    RUN mvn clean package spring-boot:repackage -DskipTests -B
    
    # --- STAGE 3: slim runtime image ---
    FROM eclipse-temurin:17-jre-alpine
    WORKDIR /app
    
    # grab the single fat JAR from target
    COPY --from=builder /app/target/*.jar app.jar
    
    ENTRYPOINT ["java","-jar","app.jar"]
    