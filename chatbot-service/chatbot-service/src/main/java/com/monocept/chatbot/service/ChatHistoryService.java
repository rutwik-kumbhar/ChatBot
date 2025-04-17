package com.monocept.chatbot.service;

import com.monocept.chatbot.model.dto.MessageDto;
import org.springframework.data.domain.Page;


public interface ChatHistoryService {

    Page<MessageDto> getMessagesFromDB(String email, int page, int size);
    Page<MessageDto> getChatHistory(String email, int page, int size);

}
