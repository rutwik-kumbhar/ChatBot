FROM openjdk:21-jdk-slim

# Install Nginx
RUN apt-get update && \
    apt-get install -y nginx && \
    rm -rf /var/lib/apt/lists/*

# Copy Nginx configuration
COPY default.conf /etc/nginx/conf.d/default.conf

# Copy Spring Boot application
COPY target/chatbot-service-0.0.1-SNAPSHOT.jar /app/chatbot-service.jar

# Expose ports
EXPOSE 80 9095

# Start both Nginx and Spring Boot using supervisord or background trick
CMD sh -c "service nginx start && java -jar /app/chatbot-service.jar"
