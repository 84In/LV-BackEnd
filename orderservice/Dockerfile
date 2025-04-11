# Build: Use Maven for building

FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

COPY ./orderservice ./orderservice
COPY ./commonservice ./commonservice
COPY ./data ./data

WORKDIR /app/commonservice
RUN mvn clean install -DskipTests

WORKDIR /app/orderservice
RUN mvn clean package -DskipTests

# Run: Use OpenJDK for running

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/orderservice/target/*.jar orderservice.jar
EXPOSE 9003
ENTRYPOINT ["java", "-jar", "orderservice.jar"]


