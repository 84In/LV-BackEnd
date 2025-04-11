# Build: Use Maven for building

FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

COPY ./productservice ./productservice
COPY ./commonservice ./commonservice
COPY ./data ./data

WORKDIR /app/commonservice
RUN mvn clean install -DskipTests

WORKDIR /app/productservice
RUN mvn clean package -DskipTests

# Run: Use OpenJDK for running

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/productservice/target/*.jar productservice.jar
EXPOSE 9002
ENTRYPOINT ["java", "-jar", "productservice.jar"]


