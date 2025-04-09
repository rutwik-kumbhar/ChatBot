package com.monocept.chatbot.service.impl;
import com.monocept.chatbot.Entity.Message;
import com.monocept.chatbot.enums.*;
import com.monocept.chatbot.exceptions.ResourcesNotFoundException;
import com.monocept.chatbot.model.dto.MediaDto;
import com.monocept.chatbot.model.dto.MessageDto;
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
import java.time.ZoneId;
import java.time.ZonedDateTime;
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

    public Page<MessageDto> getMessagesFromLast90Days(String email, int page, int size) {
        Message message = new Message();
        message.setId(5L);
        message.setUserId("user5");
        message.setEmail("user5@example.com");
        message.setSendType(MessageSendType.MESSAGE);
        message.setMessageType(MessageType.TEXT);
        message.setMessageId("msg-001");
        message.setMessageTo(MessageTo.USER);
        message.setText("Hello,how are you.");
        message.setReplyToMessageId("msg-0001");
        message.setStatus(MessageStatus.READ);
        message.setEmoji("\uD83D\uDE0A");
        message.setAction(Action.SELECTED);
        message.setMedia(new MediaDto());
        message.setOptions(List.of("Option 1", "Option 2", "Option 3"));
        message.setBotOptions(true);
        message.setPlatform("mspace");
        message.setCreatedAt(ZonedDateTime.now());
        message.setUpdatedAt(ZonedDateTime.now());
        storeNewMessageInRedis(email, message);

        Pageable pageable = PageRequest.of(page, size);
        List<MessageDto> finalMessages = new ArrayList<>();

        // Fetch messages from Redis
        try {
            List<MessageDto> recentMessagesFromRedis = redisChatHistoryRepository.getChatHistoryDetailsEmail(email);
            Page<MessageDto> recentMessagesPage = convertListToPage(recentMessagesFromRedis, pageable);
            if (recentMessagesPage != null && !recentMessagesPage.isEmpty()) {
                finalMessages.addAll(recentMessagesPage.getContent());
                logger.info("Found {} recent messages from Redis for email: {}", finalMessages.size(), email);

                // Fetch remaining messages from the database for the past 87 days
                ZonedDateTime dateTime87DaysAgo = LocalDateTime.now()
                        .minusDays(87)
                        .atZone(ZoneId.systemDefault());
                try {
                    Page<MessageDto> messagesFromDb87Days = chatHistoryRepository.findMessagesFromDayswrtEmail(dateTime87DaysAgo, email, pageable);
                    if (messagesFromDb87Days != null && !messagesFromDb87Days.isEmpty()) {
                        finalMessages.addAll(messagesFromDb87Days.getContent());
                        logger.info("Found {} messages from the database for the past 87 days for email: {}", messagesFromDb87Days.getContent().size(), email);
                    }
                } catch (Exception e) {
                    logger.error("Error fetching messages from database for the past 87 days for email {}: {}", email, e.getMessage());
                }
            } else {
                logger.info("No recent messages found in Redis, fetching from database for the last 90 days for email: {}", email);

                ZonedDateTime dateTime90DaysAgo = LocalDateTime.now()
                        .minusDays(90)
                        .atZone(ZoneId.systemDefault());
                try {
                    Page<MessageDto> messagesFromDb90Days = chatHistoryRepository.findMessagesFromDayswrtEmail(dateTime90DaysAgo, email, pageable);
                    if (messagesFromDb90Days != null && !messagesFromDb90Days.isEmpty()) {
                        finalMessages.addAll(messagesFromDb90Days.getContent());
                        logger.info("Found {} messages from the database for the past 90 days for email: {}", messagesFromDb90Days.getContent().size(), email);
                    } else {
                        logger.error("No messages found in the database for email: {} in the last 90 days", email);
                        throw new ResourcesNotFoundException("No messages found for the provided email in the last 90 days.");
                    }
                } catch (Exception e) {
                    logger.error("Error fetching messages from database for the last 90 days for email {}: {}", email, e.getMessage());
                    throw new ResourcesNotFoundException("Error fetching messages from database for the provided email in the last 90 days.");
                }
            }
        } catch (Exception e) {
            logger.error("Error retrieving messages for email {}: {}", email, e.getMessage());
            throw new ResourcesNotFoundException("An error occurred while retrieving messages for the provided email.");
        }

        // Handle empty result
        if (finalMessages.isEmpty()) {
            logger.error("No messages found for email: {} in the last 90 days", email);
            throw new ResourcesNotFoundException("No messages found for the provided email in the last 90 days.");
        }

        return new PageImpl<>(finalMessages, pageable, finalMessages.size());
    }

    public void storeNewMessageInRedis(String email, Message message) {
        // Store the new message both in Redis and DB
        try {
            logger.info("Storing new message in Redis and DB for email: {}", email);
            redisChatHistoryRepository.saveChatHistoryDetails(email, message);

            // Asynchronously move the message to DB if needed
           // moveMessageToDbAsync(message);
        } catch (Exception e) {
            logger.error("Error storing message in Redis for email {}: {}", email, e.getMessage());
            throw new RuntimeException("Error storing new message in Redis.");
        }
    }

  /*  @Async
    @Transactional
    public void moveMessageToDbAsync(Message message) {
        try {
            logger.info("Asynchronously saving message to DB: {}", message);
            chatHistoryRepository.save(message);
        } catch (Exception e) {
            logger.error("Error saving message asynchronously to DB: {}", e.getMessage());
        }
    }*/

    // Convert List to Page
    public Page<MessageDto> convertListToPage(List<MessageDto> list, Pageable pageable) {
        try {
            int start = Math.min((int) pageable.getOffset(), list.size());
            int end = Math.min((start + pageable.getPageSize()), list.size());
            if (start >= end) {
                return new PageImpl<>(new ArrayList<>(), pageable, list.size());
            }
            List<MessageDto> sublist = list.subList(start, end);
            return new PageImpl<>(sublist, pageable, list.size());
        } catch (Exception e) {
            logger.error("Error converting list to page: {}", e.getMessage());
            throw new RuntimeException("Error converting list to page.");
        }
    }
}