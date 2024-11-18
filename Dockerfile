FROM openjdk:17-jdk-slim

VOLUME /app

COPY build/libs/monitoring-proxy-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
