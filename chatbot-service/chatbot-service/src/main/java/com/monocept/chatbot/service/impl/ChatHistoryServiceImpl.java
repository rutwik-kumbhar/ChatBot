package com.monocept.chatbot.service.impl;

import com.monocept.chatbot.Entity.History;
import com.monocept.chatbot.exceptions.ResourcesNotFoundException;
import com.monocept.chatbot.model.dto.HistoryDTO;
import com.monocept.chatbot.Exception.InvalidEmailException;
import com.monocept.chatbot.reposiotry.ChatHistoryRepository;
import com.monocept.chatbot.service.ChatHistoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatHistoryServiceImpl implements ChatHistoryService {

    private final ChatHistoryRepository chatHistoryRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final com.monocept.chatbot.repository.RedisChatHistoryRepository redisChatHistoryRepository;

    public ChatHistoryServiceImpl(ChatHistoryRepository chatHistoryRepository, RedisTemplate<String, Object> redisTemplate, com.monocept.chatbot.repository.RedisChatHistoryRepository redisChatHistoryRepository) {
        this.chatHistoryRepository = chatHistoryRepository;
        this.redisTemplate = redisTemplate;
        this.redisChatHistoryRepository = redisChatHistoryRepository;
    }

    @Override
    @Transactional
    public Page<HistoryDTO> getMessagesFromLast90Days(String email, int page, int size) {
        if (email == null || email.isEmpty()) {
            throw new InvalidEmailException("Email is null or empty");
        }
        // Get the 3-day chat history from Redis
        LocalDateTime dateTime3DaysAgo = LocalDateTime.now().minusDays(3);
        List<HistoryDTO> chatHistoryDetailsEmail = redisChatHistoryRepository.getChatHistoryDetailsEmail(email);
        Pageable pageable = PageRequest.of(page, size);
        Page<HistoryDTO> recentMessagesFromRedis = convertListToPage(chatHistoryDetailsEmail, pageable);
        List<HistoryDTO> finalMessages = new ArrayList<>();
        if (recentMessagesFromRedis != null && !recentMessagesFromRedis.isEmpty()) {
             finalMessages.addAll(recentMessagesFromRedis.getContent());
        }
        //  fetch the remaining messages for the next 87 days from the database
        LocalDateTime dateTime87DaysAgo = LocalDateTime.now().minusDays(87);
        Page<HistoryDTO> messagesFromDb87Days = chatHistoryRepository.findMessagesFromDayswrtEmail(dateTime87DaysAgo, email, pageable);
        if (messagesFromDb87Days.isEmpty() && finalMessages.isEmpty()) {
            throw new ResourcesNotFoundException("No messages found for the provided email in the last 90 days.");
        }
        // Add 87 days' data to the final list
        finalMessages.addAll(messagesFromDb87Days.getContent());
        // Calculate total elements combined count of messages from Redis and the database
        long totalElements = finalMessages.size();
        // Convert final list back to a Page
        return new PageImpl<>(finalMessages, pageable, totalElements);
    }

    @Transactional
    public void storeNewMessageInRedis(String email, History newMessage) {
        // Validate that the message has necessary fields (you can add more validation based on your needs)
        if (newMessage == null || email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Invalid email or message");
        }

        // Set the current date and time if it's not already set
        if (newMessage.getDateTime() == null) {
            newMessage.setDateTime(LocalDateTime.now());
        }
        // Save the new message in Redis
        redisChatHistoryRepository.saveChatHistoryDetails(email,newMessage);
         //moveOldMessagesToDb(email);
    }
    @Transactional
    public void moveOldMessagesToDb(String email) {
        LocalDateTime fiveMinutesBefore = LocalDateTime.now().minus(3, ChronoUnit.DAYS).minus(5, ChronoUnit.MINUTES);
        // Fetch all messages in Redis
        List<HistoryDTO> redisMessages = redisChatHistoryRepository.getChatHistoryDetailsEmail(email);
        if (redisMessages != null && !redisMessages.isEmpty()) {
            List<History> messagesToMoveToDb = redisMessages.stream()
                    .map(dto -> {
                        History message = convertToHistory(dto);
                        return message;
                    })
                    .filter(message -> message.getDateTime().isBefore(fiveMinutesBefore))
                    .collect(Collectors.toList());

            // Save 3 days older messages to the Database
            if (!messagesToMoveToDb.isEmpty()) {
                chatHistoryRepository.saveAll(messagesToMoveToDb);
            }
        }

    }
    private Page<HistoryDTO> convertListToPage(List<HistoryDTO> list, Pageable pageable) {
        int start = Math.min((int) pageable.getOffset(), list.size());
        int end = Math.min((start + pageable.getPageSize()), list.size());
        List<HistoryDTO> sublist = list.subList(start, end);
        return new PageImpl<>(sublist, pageable, list.size());
    }

    private History convertToHistory(HistoryDTO dto) {
        History history = new History();
        history.setDateTime(dto.getDateTime());
        history.setMessageTo(dto.getMessageTo());
        // Add other necessary fields
        return history;
    }

}
