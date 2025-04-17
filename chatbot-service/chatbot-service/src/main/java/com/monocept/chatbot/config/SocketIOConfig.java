package com.monocept.chatbot.config;

import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOServer;
//import com.corundumstudio.socketio.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SocketIOConfig {

    @Value("${socketio.hostname}")
    private String hostname;

    @Value("${socketio.port}")
    private int port;

    @Value("${socketio.allowed-origins}")
    private String allowedOrigins;

    @Bean
    public SocketIOServer socketIOServer() {
        System.out.println("=== Initializing SocketIO Server ===");

        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname(hostname);
        config.setPort(port);
        config.setOrigin(allowedOrigins);
        config.setAllowCustomRequests(true); // Allows URL parameters
        config.setUpgradeTimeout(10000); // Increase timeout
        config.setPingInterval(25000);
        config.setPingTimeout(60000);

        // Socket configuration
        SocketConfig socketConfig = new SocketConfig();
        socketConfig.setReuseAddress(true);
        config.setSocketConfig(socketConfig);

        // Allow all origins (for development only)
        config.setOrigin("*");
        return new SocketIOServer(config);

    }
}