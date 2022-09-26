FROM openjdk:11-jdk
COPY build/libs/*SNAPSHOT.jar app.jar

EXPOSE 8090
ENTRYPOINT ["java", "-jar", "/app.jar"]