FROM openjdk:21-jdk-slim

WORKDIR /app

COPY services/ads-service/build/libs/*.jar ads-service.jar

CMD ["java", "-jar", "ads-service.jar"]