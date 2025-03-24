# Build: Use Maven for building

FROM maven:3.9.9-eclipse-temurin-21-alpine as build
WORKDIR /app

COPY ./userservice ./userservice
COPY ./commonservice ./commonservice

WORKDIR /app/commonservice
RUN mvn clean install -DskipTests

WORKDIR /app/userservice
RUN mvn clean package -DskipTests

# Run: Use OpenJDK for running

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/userservice/target/*.jar userservice.jar
EXPOSE 9000
ENTRYPOINT ["java", "-jar", "userservice.jar"]


