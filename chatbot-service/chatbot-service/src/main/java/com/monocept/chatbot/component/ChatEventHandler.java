package com.monocept.chatbot.component;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.monocept.chatbot.model.request.SendMessageRequest;
import com.monocept.chatbot.service.MessageService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
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
        server.addEventListener("message", SendMessageRequest.class, onMessageReceived());
        server.addDisconnectListener(onDisconnected());
        server.addEventListener("chat_message", SendMessageRequest.class, messageResponse());
    }

    public DataListener<SendMessageRequest> onMessageReceived() {
        return (client, message, ackSender) -> {
            System.out.println(message);
            log.info(message.toString());

//            messageService.processMessage(message);
        };
    }

    public DataListener<SendMessageRequest> messageResponse() {
        return (client, message, ackSender) -> {
            System.out.println("--------chat_message------------");
            System.out.println(message);
            // Extract user ID from handshake
            String userId = client.getHandshakeData().getSingleUrlParam("userId");
            String sessionId = client.getHandshakeData().getSingleUrlParam("sessionId");

//            client.sendEvent("chat_message", "botResponse");
//            socketService.saveBotResponse(botResponse, userId, senderClient.getSessionId().toString());
        };
    }

    public ConnectListener onConnected() {
        return client -> {
            String sessionId = client.getSessionId().toString();
            log.info("Connected to chat session " + sessionId);

            // More robust parameter handling
            String userId = Optional.ofNullable(client.getHandshakeData().getSingleUrlParam("userId"))
                    .orElse("anonymous_" + client.getSessionId().toString());

            clientManager.registerClient(userId, client);
            redisTemplate.opsForHash().put("session:user", userId, sessionId);
        };
    }

    private DisconnectListener onDisconnected() {
        return client -> {
            String userId = client.getHandshakeData().getSingleUrlParam("userId");

            clientManager.removeClient(userId);
            // Remove session from Redis
            redisTemplate.opsForHash().delete("session:user", userId);
            log.info("Disconnected from chat session " + userId);
        };
    }
}
