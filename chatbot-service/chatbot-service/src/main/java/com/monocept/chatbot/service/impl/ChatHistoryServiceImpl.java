package com.monocept.chatbot.service.impl;

import com.monocept.chatbot.exceptions.ResourcesNotFoundException;
import com.monocept.chatbot.model.dto.HistoryDTO;
import com.monocept.chatbot.Exception.InvalidEmailException;
import com.monocept.chatbot.reposiotry.ChatHistoryRepository;
import com.monocept.chatbot.service.ChatHistoryService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class ChatHistoryServiceImpl implements ChatHistoryService {

    private final ChatHistoryRepository chatHistoryRepository;
    private static final Logger logger = LoggerFactory.getLogger(ChatHistoryServiceImpl.class);
    public ChatHistoryServiceImpl(ChatHistoryRepository chatHistoryRepository) {
        this.chatHistoryRepository = chatHistoryRepository;
    }

    @Override
    @Transactional
    public Page<HistoryDTO> getMessagesFromLast90Days(String email,int page, int size) {
        if (email == null || email.isEmpty()) {
            logger.error("Email is null or empty");
            throw new InvalidEmailException("Email is null or empty");
        }
        logger.info("Fetching messages for email: {}", email);
        try {
        // Calculate the dateTime 90 days ago from the current date
        LocalDateTime dateTime90DaysAgo = LocalDateTime.now().minus(90, ChronoUnit.DAYS);
        // Create a Pageable object for pagination
        Pageable pageable = PageRequest.of(page, size);

        Page<HistoryDTO> messages = chatHistoryRepository.findMessagesFromLast90DayswrtEmail(dateTime90DaysAgo,email,pageable);

        if (messages.isEmpty()) {
            logger.warn("No messages found for email: {}", email);
            throw new ResourcesNotFoundException("No messages found for the provided email in the last 90 days.");
        }
        return messages;
        } catch (Exception ex) {
            logger.error("An error occurred while fetching messages for email: {}. Error: {}", email, ex.getMessage(), ex);
            throw new RuntimeException("An error occurred while processing the request.", ex);
        }

    }
}