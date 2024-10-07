FROM openjdk:21-jdk-slim

WORKDIR /app

COPY services/payment-service/build/libs/*.jar payment-service.jar

CMD ["java", "-jar", "payment-service.jar"]