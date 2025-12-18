# Stage 1: build with Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: run with JDK
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
EXPOSE 8085
COPY --from=build /app/target/medilabo-gateway-*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]