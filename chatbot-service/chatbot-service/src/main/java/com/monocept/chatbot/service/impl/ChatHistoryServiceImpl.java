package com.monocept.chatbot.service.impl;

import com.monocept.chatbot.model.ChatHistory;
import com.monocept.chatbot.repository.ChatHistoryRepository;
import com.monocept.chatbot.service.ChatHistoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatHistoryServiceImpl implements ChatHistoryService {

    private final ChatHistoryRepository chatHistoryRepository;

    public ChatHistoryServiceImpl(ChatHistoryRepository chatHistoryRepository) {
        this.chatHistoryRepository = chatHistoryRepository;
    }

    @Override
    public List<ChatHistory> getChatHistoryByEmail(String email) {
        return chatHistoryRepository.findByEmailOrderByDateTimeAsc(email);
    }

}
