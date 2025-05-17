FROM maven:3.8.3-openjdk-17-slim AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# This puts your jar at /app/app.jar
COPY --from=builder /app/target/*.jar app.jar

# Now run it by name inside the WORKDIR
ENTRYPOINT ["java","-jar","app.jar"]
