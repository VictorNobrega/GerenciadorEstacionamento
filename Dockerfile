FROM gradle:8.14.3-jdk21 AS build
WORKDIR /workspace
COPY --chown=gradle:gradle . .
RUN gradle clean bootJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/build/libs/*.jar app.jar
EXPOSE 3003
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
