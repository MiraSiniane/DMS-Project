# Then paste this content:

FROM openjdk:17-slim

WORKDIR /app

COPY target/storage-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]