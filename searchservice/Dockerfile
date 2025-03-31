
FROM maven:3.9.9-eclipse-temurin-21-alpine as build
WORKDIR /app

COPY ./searchservice ./searchservice
COPY ./commonservice ./commonservice
COPY ./data ./data

WORKDIR /app/commonservice
RUN mvn clean install -DskipTests

WORKDIR /app/searchservice
RUN mvn clean package -DskipTests

# Run: Use OpenJDK for running

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/searchservice/target/*.jar searchservice.jar
EXPOSE 9002
ENTRYPOINT ["java", "-jar", "searchservice.jar"]
