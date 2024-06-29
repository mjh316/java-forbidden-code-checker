FROM openjdk:17
workdir /app
COPY ${JAR_FILE} app.jar
EXPOSE 8080
CMD [ "java", "-jar",  "demo.jar"]