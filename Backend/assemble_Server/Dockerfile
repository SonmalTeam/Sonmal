FROM openjdk:11-jdk AS builder
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src
RUN chmod +x ./gradlew
RUN ./gradlew clean build 

FROM openjdk:11-jdk
COPY --from=builder build/libs/*.jar app.jar

EXPOSE 8090
ENTRYPOINT ["java", "-jar", "/app.jar"]
