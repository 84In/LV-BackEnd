# Build: Use Maven for building

FROM maven:3.9.9-eclipse-temurin-21-alpine as build
WORKDIR /app

COPY ./mediaservice ./mediaservice
COPY ./commonservice ./commonservice

WORKDIR /app/commonservice
RUN mvn clean install -DskipTests

WORKDIR /app/mediaservice
RUN mvn dependency:resolve
RUN mvn clean package -DskipTests

# Run: Use OpenJDK for running

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/mediaservice/target/*.jar mediaservice.jar
EXPOSE 9001
ENTRYPOINT ["java", "-jar", "mediaservice.jar"]


