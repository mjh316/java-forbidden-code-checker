FROM openjdk:17

COPY demo/target/demo-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080

CMD [ "java", "-jar",  "/app.jar"]