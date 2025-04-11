# Build: Use Maven for building

FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

COPY ./chatservice ./chatservice
COPY ./commonservice ./commonservice

WORKDIR /app/commonservice
RUN mvn clean install -DskipTests

WORKDIR /app/chatservice
RUN mvn clean package -DskipTests

# Run: Use OpenJDK for running

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/chatservice/target/*.jar chatservice.jar
EXPOSE 9001
ENTRYPOINT ["java", "-jar", "chatservice.jar"]


