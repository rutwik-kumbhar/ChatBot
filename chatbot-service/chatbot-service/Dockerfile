FROM openjdk:21-jdk-slim
WORKDIR /app
EXPOSE 9095
EXPOSE 5050
ADD chatbot-service/target/chatbot-service-0.0.1-SNAPSHOT.jar chatbot-service-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "/app/chatbot-service-0.0.1-SNAPSHOT.jar"]
