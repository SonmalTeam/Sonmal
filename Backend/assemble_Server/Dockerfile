FROM openjdk:11-jdk
COPY --from=builder build/libs/*.jar app.jar

EXPOSE 8090
ENTRYPOINT ["java", "-jar", "/app.jar"]
