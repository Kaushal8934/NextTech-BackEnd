FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the JAR from the build stage
# Note: Ensure your pom.xml doesn't change the finalName;
# if it does, replace *.jar with your specific filename.

COPY --from=build /app/target/backend-0.0.1-SNAPSHOT.jar app.jar

# Render uses a dynamic port; this tells Spring to listen to it
EXPOSE 8080

# Run the application with memory constraints for Render's Free Tier
ENTRYPOINT ["java", "-Xmx512m", "-jar", "app.jar", "--server.port=${PORT:8080}"]