FROM openjdk:21-jdk-slim

WORKDIR /app

COPY services/saga-orchestrator/build/libs/*.jar saga-orchestrator.jar

CMD ["java", "-jar", "saga-orchestrator.jar"]