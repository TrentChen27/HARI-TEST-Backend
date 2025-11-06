FROM openjdk:17-slim

WORKDIR /app

COPY target/hari-exercise-tracker-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
