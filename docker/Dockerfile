# Stage 1: build the jar
FROM gradle:jdk21 AS builder
WORKDIR /home/gradle/project

# Copy everything and build
COPY . .

RUN ./gradlew clean
RUN ./gradlew assemble --no-daemon

# Stage 2: run the app
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the fat JAR produced by the builder
COPY --from=builder /home/gradle/project/build/libs/*.jar app.jar

# Expose your app port
EXPOSE 8080

# Launch
ENTRYPOINT ["java", "-jar", "app.jar"]
