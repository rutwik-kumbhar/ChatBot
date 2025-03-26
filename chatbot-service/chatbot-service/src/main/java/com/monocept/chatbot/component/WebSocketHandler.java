package com.monocept.chatbot.component;

import com.monocept.chatbot.service.SendMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;


@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private static final Logger log = LoggerFactory.getLogger(WebSocketHandler.class);

    private final SendMessageService sendMessageService;

    public WebSocketHandler(SendMessageService sendMessageService) {
        this.sendMessageService = sendMessageService;
    }

    private static final CopyOnWriteArraySet<WebSocketSession> sessions = new CopyOnWriteArraySet<>();


    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("WebSocket connection established. Session ID: {}", session.getId());
        sessions.add(session);
        broadcast("WebSocket connection established");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String userMessage = message.getPayload();
        sendMessageService.send(userMessage);
    }

    public void broadcast(String message) {
        try {

            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(message)); // Send JSON as a WebSocket message
                }
            }
        } catch (Exception e) {
            log.error("Failed to send WebSocket message", e);
        }
    }
}
