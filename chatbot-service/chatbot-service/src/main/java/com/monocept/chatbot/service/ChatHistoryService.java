package com.monocept.chatbot.service;

import com.monocept.chatbot.model.dto.HistoryDTO;
import org.springframework.data.domain.Page;


public interface ChatHistoryService {

    Page<HistoryDTO> getMessagesFromLast90Days(String email, int page, int size);
}
