package com.monocept.chatbot.component;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import jakarta.annotation.PostConstruct;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ChatEventHandler {

    private final SocketIOServer server;
    private final RedisTemplate<String, String> redisTemplate;
    private SocketClientManager clientManager;
    public ChatEventHandler(SocketIOServer server, RedisTemplate<String, String> redisTemplate, SocketClientManager clientManager) {
        this.server = server;
        this.server.start();
        this.redisTemplate = redisTemplate;
        this.clientManager = clientManager;
    }

    @PostConstruct
    public void setupListeners() {
        server.addConnectListener(onConnected());
        server.addEventListener("message", String.class, onMessageReceived());
        server.addDisconnectListener(onDisconnected());
        server.addEventListener("chat_message", String.class, messageResponse());
    }

    public DataListener<String> onMessageReceived() {
        return (client, message, ackSender) -> {
            System.out.println("--------message------------");
            System.out.println(message);
            // Extract user ID from handshake
            String userId = client.getHandshakeData().getSingleUrlParam("userId");
            String sessionId = client.getHandshakeData().getSingleUrlParam("sessionId");

            // 3. Send response and save to DB
            client.sendEvent("chat_message", "botResponse");
//            socketService.saveBotResponse(botResponse, userId, senderClient.getSessionId().toString());
        };
    }

    public DataListener<String> messageResponse() {
        return (client, message, ackSender) -> {
            System.out.println("--------chat_message------------");
            System.out.println(message);
            // Extract user ID from handshake
            String userId = client.getHandshakeData().getSingleUrlParam("userId");
            String sessionId = client.getHandshakeData().getSingleUrlParam("sessionId");

            client.sendEvent("chat_message", "botResponse");
//            socketService.saveBotResponse(botResponse, userId, senderClient.getSessionId().toString());
        };
    }

    public ConnectListener onConnected() {
        return client -> {
            System.out.println("=== HANDSHAKE DATA ===");
            String sessionId = client.getSessionId().toString();
            System.out.println("from handler : SessionId: " + sessionId);

            // More robust parameter handling
            String userId = Optional.ofNullable(client.getHandshakeData().getSingleUrlParam("userId"))
                    .orElse("anonymous_" + client.getSessionId().toString());
            clientManager.registerClient(userId, client);
            redisTemplate.opsForHash().put("session:user", userId, sessionId);
            // Immediate welcome message
            String welcomeMsg = "Hello! Your user ID is: " + userId;
            client.sendEvent("chat_message", welcomeMsg);
        };
    }

    private DisconnectListener onDisconnected() {
        return client -> {
            String userId = client.getHandshakeData().getSingleUrlParam("userId");

            clientManager.removeClient(userId);
            // Remove session from Redis
            redisTemplate.opsForHash().delete("session:user", userId);
            System.out.println("Session removed for user " + userId);
        };
    }
}
