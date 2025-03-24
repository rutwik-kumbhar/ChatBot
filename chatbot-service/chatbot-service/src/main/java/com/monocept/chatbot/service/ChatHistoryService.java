package com.monocept.chatbot.service;

import com.monocept.chatbot.model.ChatHistory;

import java.util.List;

public interface ChatHistoryService {
    List<ChatHistory> getChatHistoryByEmail(String email);
}
