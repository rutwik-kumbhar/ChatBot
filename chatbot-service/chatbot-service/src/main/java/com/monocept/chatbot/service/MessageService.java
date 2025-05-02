package com.monocept.chatbot.service;

import com.monocept.chatbot.model.dto.MessageDto;
import com.monocept.chatbot.model.dto.ReceiveMessageDTO;
import com.monocept.chatbot.model.request.SendMessageRequest;
import com.monocept.chatbot.model.response.ReceiveMessageResponse;
import com.monocept.chatbot.model.response.SendMessageResponse;

public interface MessageService {
    void processMessage(SendMessageRequest request);

    ReceiveMessageResponse receiveMessage(ReceiveMessageDTO receiveMessageDTO);
}
