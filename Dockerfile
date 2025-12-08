# Stage 1: Build the application
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml ./
COPY src ./src
RUN mvn clean install -U

# Stage 2: Create the final runtime image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Create a non-root user for enhanced security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring
# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]