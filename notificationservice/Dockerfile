# Build: Use Maven for building

FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

COPY ./notificationservice ./notificationservice
COPY ./commonservice ./commonservice

WORKDIR /app/commonservice
RUN mvn clean install -DskipTests

WORKDIR /app/notificationservice
RUN mvn clean package -DskipTests

# Run: Use OpenJDK for running

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/notificationservice/target/*.jar notificationservice.jar
EXPOSE 9004
ENTRYPOINT ["java", "-jar", "notificationservice.jar"]


