# Build: Use Maven for building

FROM maven:3.9.9-eclipse-temurin-21-alpine as build
WORKDIR /app

COPY ./apigateway ./apigateway

WORKDIR /app/apigateway
RUN mvn clean package -DskipTests

# Run: Use OpenJDK for running

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/apigateway/target/*.jar apigateway.jar
EXPOSE 9001
ENTRYPOINT ["java", "-jar", "apigateway.jar"]


