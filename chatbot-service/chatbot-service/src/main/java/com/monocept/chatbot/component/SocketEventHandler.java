package com.monocept.chatbot.component;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.monocept.chatbot.entity.Message;
import com.monocept.chatbot.model.dto.ReceiveMessageDTO;
import com.monocept.chatbot.model.request.SendMessageRequest;
import com.monocept.chatbot.model.response.MasterResponse;
import com.monocept.chatbot.service.MessageService;
import com.monocept.chatbot.utils.RedisUtility;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class SocketEventHandler {

    private final SocketIOServer server;
    private final RedisTemplate<String, String> redisTemplate;
    private final MessageService messageService;
    private final RedisUtility redisUtility;

    public SocketEventHandler(SocketIOServer server, RedisTemplate<String, String> redisTemplate, MessageService messageService, RedisUtility redisUtility) {
        this.server = server;
        this.server.start();
        this.redisTemplate = redisTemplate;
        this.messageService = messageService;
        this.redisUtility = redisUtility;
    }

    @PostConstruct
    public void setupListeners() {
        server.addConnectListener(socketConnected());
        server.addEventListener("user_message", SendMessageRequest.class, socketSendMessage());
        server.addDisconnectListener(socketDisconnected());
        server.addEventListener("bot_message", ReceiveMessageDTO.class, socketReceiveMessage());
        server.addEventListener("acknowledgement", MasterResponse.class, socketReceiveAcknowledge());
    }

    public DataListener<SendMessageRequest> socketSendMessage() {
        return (client, message, ackSender) -> {
            log.info("user_message: {}", message.toString());
            messageService.processMessage(message);
        };
    }

    public DataListener<ReceiveMessageDTO> socketReceiveMessage() {
        return (client, message, ackSender) -> {
            log.info("bot_message: {}", message.toString());
        };
    }

    public DataListener<MasterResponse> socketReceiveAcknowledge() {
        return (client, message, ackSender) -> {
            log.info("acknowledgement: {}", message.toString());
        };
    }

    public ConnectListener socketConnected() {
        return client -> {
            String sessionId = client.getSessionId().toString();
            log.info("socketConnected : {}", sessionId);

            // More robust parameter handling
            String userId = Optional.ofNullable(client.getHandshakeData().getSingleUrlParam("userId"))
                    .orElse("anonymous_" + client.getSessionId().toString());

            redisTemplate.opsForHash().put("session:user", userId, sessionId);
            redisTemplate.opsForHash().put("socket:user", userId, sessionId);
        };
    }

    private DisconnectListener socketDisconnected() {
        return client -> {
            String userId = client.getHandshakeData().getSingleUrlParam("userId");
            redisTemplate.opsForHash().delete("session:user", userId);
            List<Message> messages = redisUtility.getLiveMessagesFromRedisSortedSet(userId);
            redisUtility.saveMessagesToRedisSortedSet(userId, messages);
            log.info("Disconnected from socket: {}", userId);
        };
    }
}
