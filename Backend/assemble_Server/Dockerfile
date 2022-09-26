FROM openjdk:11-jdk
COPY build/libs/assemble_Server-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8090
ENTRYPOINT ["java", "-jar", "/app.jar"]