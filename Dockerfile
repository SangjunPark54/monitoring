FROM openjdk:21-jdk-slim

VOLUME /app

COPY build/libs/metric-agent-0.0.1-SNAPSHOT.jar app.jar

ENV SPRING_ACTIVE_PROFILE prd
ENV JAVA_ARGS -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=${SPRING_ACTIVE_PROFILE}

ENTRYPOINT ["/bin/sh", "-c", "java ${JAVA_ARGS} -jar /app.jar"]
