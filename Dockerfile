FROM openjdk:16-jdk-alpine AS coolstart

ARG JAR_FILE=build/libs/*SNAPSHOT.jar

ADD ${JAR_FILE} app.jar

ENTRYPOINT ["sh", "-c", "java -jar /app.jar"]