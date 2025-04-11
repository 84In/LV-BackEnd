# Build: Use Maven for building

FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY ./discoveryserver ./discoveryserver
WORKDIR /app/discoveryserver
RUN mvn clean package -DskipTests

# Run: Use OpenJDK for running

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/discoveryserver/target/*.jar discoveryserver.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "discoveryserver.jar"]


