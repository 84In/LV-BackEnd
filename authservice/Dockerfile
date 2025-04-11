# Build: Use Maven for building

FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

COPY ./authservice ./authservice
COPY ./commonservice ./commonservice

WORKDIR /app/commonservice
RUN mvn clean install -DskipTests

WORKDIR /app/authservice
RUN mvn clean package -DskipTests

# Run: Use OpenJDK for running

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/authservice/target/*.jar authservice.jar
EXPOSE 9009
ENTRYPOINT ["java", "-jar", "authservice.jar"]


