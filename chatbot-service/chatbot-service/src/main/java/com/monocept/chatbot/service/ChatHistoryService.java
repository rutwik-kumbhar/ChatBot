package com.monocept.chatbot.service;

import com.monocept.chatbot.ChatDTO.HistoryDTO;
import com.monocept.chatbot.Entity.History;

import java.util.List;

public interface ChatHistoryService {

    List<HistoryDTO> getMessagesFromLast90Days(String email);
}
