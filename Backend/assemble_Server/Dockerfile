FROM openjdk:11-jdk
COPY build/libs/*SNAPSHOT.jar app.jar

VOLUME ["/home/ubuntu/docker-volume/files"]

EXPOSE 8090
ENTRYPOINT ["java", "-jar", "/app.jar"]