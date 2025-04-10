FROM gradle:7.6-jdk17 as build
# Set container working directory to /app
WORKDIR /app
# Copy Gradle configuration files
COPY gradlew gradlew.bat /app/
COPY gradle /app/gradle
# Ensure Gradle wrapper is executable
RUN chmod +x ./gradlew
# Copy build script and source code
COPY build.gradle settings.gradle /app/
COPY src /app/src
# Build the server
RUN --mount=type=secret,id=GOOGLE_MAPS_API_KEY \
    export GOOGLE_MAPS_API_KEY=$(cat /run/secrets/GOOGLE_MAPS_API_KEY) && \
    ./gradlew clean build --no-daemon

# make image smaller by using multi stage build
FROM openjdk:17-slim
# Set the env to "production"
ENV SPRING_PROFILES_ACTIVE=production
# get non-root user
USER 3301
# Set container working directory to /app
WORKDIR /app
# copy built artifact from build stage
COPY --from=build /app/build/libs/*.jar /app/soprafs24.jar
# Expose the port on which the server will be running (based on application.properties)
EXPOSE 8080
# start server with environment variable
CMD --mount=type=secret,id=GOOGLE_MAPS_API_KEY \
    export GOOGLE_MAPS_API_KEY=$(cat /run/secrets/GOOGLE_MAPS_API_KEY) && \
    java -jar /app/soprafs24.jar