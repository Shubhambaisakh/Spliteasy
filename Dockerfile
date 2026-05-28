# Multi-stage Dockerfile for SplitEase (Spring Boot + Java 17)

# Stage 1: Build the application using Maven and JDK 17
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code and build the package (skipping tests for faster build)
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Create a minimal JRE runtime image for execution
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the compiled JAR file from the builder stage
COPY --from=build /app/target/splitease-1.0.0.jar app.jar

# Expose the port (Railway automatically injects the PORT env var)
ENV PORT=8080
EXPOSE 8080

# Execute the application
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
