package com.monocept.chatbot.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monocept.chatbot.component.WebSocketHandler;
import com.monocept.chatbot.service.ReceivedMessageSocketService;
import org.springframework.stereotype.Service;

@Service
public class ReceivedMessageSocketServiceImpl implements ReceivedMessageSocketService {

    private final WebSocketHandler webSocketHandler;
    private final ObjectMapper objectMapper;

    public ReceivedMessageSocketServiceImpl(WebSocketHandler webSocketHandler, ObjectMapper objectMapper) {
        this.webSocketHandler = webSocketHandler;
        this.objectMapper = objectMapper;
    }

    @Override
    public void receive(Object message) throws JsonProcessingException {
        String jsonMessage = objectMapper.writeValueAsString(message); // Convert Object DTO to JSON
         webSocketHandler.broadcast(jsonMessage);
    }
}
