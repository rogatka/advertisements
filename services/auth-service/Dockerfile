FROM openjdk:21-jdk-slim

WORKDIR /app

COPY services/auth-service/build/libs/*.jar auth-service.jar

CMD ["java", "-jar", "auth-service.jar"]