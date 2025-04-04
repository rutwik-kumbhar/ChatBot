package com.monocept.chatbot.service.impl;

import com.monocept.chatbot.Entity.History;
import com.monocept.chatbot.exceptions.ResourcesNotFoundException;
import com.monocept.chatbot.model.dto.HistoryDTO;
import com.monocept.chatbot.reposiotry.ChatHistoryRepository;
import com.monocept.chatbot.service.ChatHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatHistoryServiceImpl implements ChatHistoryService {
    private static final Logger logger = LoggerFactory.getLogger(ChatHistoryServiceImpl.class);

    private final ChatHistoryRepository chatHistoryRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final com.monocept.chatbot.repository.RedisChatHistoryRepository redisChatHistoryRepository;

    public ChatHistoryServiceImpl(ChatHistoryRepository chatHistoryRepository,
                                  RedisTemplate<String, Object> redisTemplate,
                                  com.monocept.chatbot.repository.RedisChatHistoryRepository redisChatHistoryRepository) {
        this.chatHistoryRepository = chatHistoryRepository;
        this.redisTemplate = redisTemplate;
        this.redisChatHistoryRepository = redisChatHistoryRepository;
    }

    @Override
    @Transactional
    public Page<HistoryDTO> getMessagesFromLast90Days(String email, int page, int size) {
        History newMessage = new History();
        newMessage.setMsgId(11L);
        newMessage.setMsg("Hello");
        newMessage.setMessageTo("user10");
        newMessage.setDateTime(LocalDateTime.now());
        newMessage.setReplyId("reply123");
        newMessage.setType("text");
        newMessage.setMediaUrl(null);
        newMessage.setActivity("Like");
        newMessage.setSsoid("SSO12345");
        newMessage.setAgentId("agent001");
        newMessage.setEmail("user11@example.com");
        newMessage.setSession("session001");
        storeNewMessageInRedis(email, newMessage);


        // Combining Redis and Database logic in one method
        Pageable pageable = PageRequest.of(page, size);
        List<HistoryDTO> finalMessages = new ArrayList<>();

        // Fetch messages from Redis
        List<HistoryDTO> recentMessagesFromRedis = redisChatHistoryRepository.getChatHistoryDetailsEmail(email);
        Page<HistoryDTO> recentMessagesPage = convertListToPage(recentMessagesFromRedis, pageable);

        if (!recentMessagesPage.isEmpty()) {
            finalMessages.addAll(recentMessagesPage.getContent());
            logger.info("Found {} recent messages from Redis", finalMessages.size());

            // Fetch remaining messages from the database for the past 87 days
            LocalDateTime dateTime87DaysAgo = LocalDateTime.now().minusDays(87);
            Page<HistoryDTO> messagesFromDb87Days = chatHistoryRepository.findMessagesFromDayswrtEmail(dateTime87DaysAgo, email, pageable);
            if (!messagesFromDb87Days.isEmpty()) {
                finalMessages.addAll(messagesFromDb87Days.getContent());
                logger.info("Found {} messages from the database for the past 87 days", messagesFromDb87Days.getContent().size());
            }
        } else {
            logger.info("No recent messages found in Redis, fetching from database for the last 90 days.");
            LocalDateTime dateTime90DaysAgo = LocalDateTime.now().minusDays(90);
            Page<HistoryDTO> messagesFromDb90Days = chatHistoryRepository.findMessagesFromDayswrtEmail(dateTime90DaysAgo, email, pageable);

            if (messagesFromDb90Days.isEmpty()) {
                logger.error("No messages found for email: {} in the last 90 days", email);
                throw new ResourcesNotFoundException("No messages found for the provided email in the last 90 days.");
            }
            finalMessages.addAll(messagesFromDb90Days.getContent());
            logger.info("Found {} messages from the database for the past 90 days", messagesFromDb90Days.getContent().size());
        }

        // Handle empty result
        if (finalMessages.isEmpty()) {
            logger.error("No messages found for email: {} in the last 90 days", email);
            throw new ResourcesNotFoundException("No messages found for the provided email in the last 90 days.");
        }


        return new PageImpl<>(finalMessages, pageable, finalMessages.size());
    }

    public void storeNewMessageInRedis(String email, History newMessage) {
        // Store the new message both in Redis and DB
        logger.info("Storing new message in Redis and DB for email: {}", email);
        redisChatHistoryRepository.saveChatHistoryDetails(email, newMessage);
        chatHistoryRepository.save(newMessage);

        // Asynchronously move the message to DB if needed
        moveMessageToDbAsync(newMessage);
    }

    @Async
    @Transactional
    public void moveMessageToDbAsync(History newMessage) {
        logger.info("Asynchronously saving message to DB: {}", newMessage);
        chatHistoryRepository.save(newMessage);
    }

    // convert List to Page
    public Page<HistoryDTO> convertListToPage(List<HistoryDTO> list, Pageable pageable) {
        int start = Math.min((int) pageable.getOffset(), list.size());
        int end = Math.min((start + pageable.getPageSize()), list.size());

        if (start >= end) {
            return new PageImpl<>(new ArrayList<>(), pageable, list.size());
        }
        List<HistoryDTO> sublist = list.subList(start, end);
        return new PageImpl<>(sublist, pageable, list.size());
    }
}
