package com.monocept.chatbot.service.impl;

import com.monocept.chatbot.ChatDTO.HistoryDTO;
import com.monocept.chatbot.Entity.History;
import com.monocept.chatbot.Exception.ChatHistoryNotFoundException;
import com.monocept.chatbot.Exception.InvalidEmailException;
import com.monocept.chatbot.Repository.ChatHistoryRepository;
import com.monocept.chatbot.service.ChatHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ChatHistoryServiceImpl implements ChatHistoryService {

    private final ChatHistoryRepository chatHistoryRepository;
    private static final Logger logger = LoggerFactory.getLogger(ChatHistoryServiceImpl.class);
    public ChatHistoryServiceImpl(ChatHistoryRepository chatHistoryRepository) {
        this.chatHistoryRepository = chatHistoryRepository;
    }

    @Override
    public List<HistoryDTO> getMessagesFromLast90Days(String email) {
        if (email == null || email.isEmpty()) {
            logger.error("Email is null or empty");
            throw new InvalidEmailException("Email is null or empty");
        }
        logger.info("Fetching messages for email: {}", email);

        // Calculate the dateTime 90 days ago from the current date
        LocalDateTime dateTime90DaysAgo = LocalDateTime.now().minus(90, ChronoUnit.DAYS);
        // Fetch messages from the repository (handle null with Optional)
        List<HistoryDTO> messages = Optional.ofNullable(chatHistoryRepository.findMessagesFromLast90DayswrtEmail(dateTime90DaysAgo,email))
                .orElse(Collections.emptyList());  // Return empty list if no messages are found

        if (messages.isEmpty()) {
            logger.warn("No messages found for email: {}", email);
            throw new ChatHistoryNotFoundException("No messages found for the provided email in the last 90 days.");
        }
        return messages;

    }
}