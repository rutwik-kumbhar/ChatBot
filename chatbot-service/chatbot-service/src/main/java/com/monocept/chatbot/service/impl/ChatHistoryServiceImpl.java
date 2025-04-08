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
    @Transactional
    public Page<MessageDto> getMessagesFromLast90Days(String email, int page, int size) {
        Message message = new Message();
        message.setId(1L);
        message.setUserId("user123");
        message.setEmail("user123@example.com");
        message.setSendType(MessageSendType.MESSAGE); // Assume the enum contains types like TEXT, IMAGE, etc.
        message.setMessageType(MessageType.TEXT); // Assume enum contains types like TEXT, IMAGE, etc.
        message.setMessageId("msg-001");
        message.setMessageTo(MessageTo.BOT); // Assume this is a predefined enum like USER, BOT, etc.
        message.setText("Hello, this is a test message.");
        message.setReplyToMessageId("msg-000"); // Assuming it's a valid previous message ID
        message.setStatus(MessageStatus.READ); // Sent, Pending, etc. based on your enum
        message.setEmoji("\uD83D\uDE0A"); // Emojis as string
        message.setAction(Action.SELECTED); // Assume Action enum contains REPLY, FORWARD, etc.
        message.setMedia(new MediaDto()); // MediaDto instance, adjust based on your actual class structure

// Sample list of bot options (assuming it's for choices or buttons)
        message.setBotOptions(List.of("Option 1", "Option 2", "Option 3"));
        message.setOption(true); // A boolean flag indicating if options are enabled
        message.setPlatform("web"); // Platform like 'web', 'mobile', etc.
        message.setCreatedAt(ZonedDateTime.now()); // Date and time of creation
        message.setUpdatedAt(ZonedDateTime.now()); // Date and time of last update


        storeNewMessageInRedis(email, message);



        // Combining Redis and Database logic in one method
        Pageable pageable = PageRequest.of(page, size);
        List<MessageDto> finalMessages = new ArrayList<>();

        // Fetch messages from Redis
        List<MessageDto> recentMessagesFromRedis = redisChatHistoryRepository.getChatHistoryDetailsEmail(email);
        Page<MessageDto> recentMessagesPage = convertListToPage(recentMessagesFromRedis, pageable);

        if (!recentMessagesPage.isEmpty()) {
            finalMessages.addAll(recentMessagesPage.getContent());
            logger.info("Found {} recent messages from Redis", finalMessages.size());

            // Fetch remaining messages from the database for the past 87 days
           // LocalDateTime dateTime87DaysAgo = LocalDateTime.now().minusDays(87);
            ZonedDateTime dateTime87DaysAgo = LocalDateTime.now()
                    .minusDays(87)
                    .atZone(ZoneId.systemDefault());
            Page<MessageDto> messagesFromDb87Days = chatHistoryRepository.findMessagesFromDayswrtEmail(dateTime87DaysAgo, email, pageable);
            if (!messagesFromDb87Days.isEmpty()) {
                finalMessages.addAll(messagesFromDb87Days.getContent());
                logger.info("Found {} messages from the database for the past 87 days", messagesFromDb87Days.getContent().size());
            }
        } else {
            logger.info("No recent messages found in Redis, fetching from database for the last 90 days.");
          //  LocalDateTime dateTime90DaysAgo = LocalDateTime.now().minusDays(90);
            ZonedDateTime dateTime90DaysAgo = LocalDateTime.now()
                    .minusDays(90)
                    .atZone(ZoneId.systemDefault());
            Page<MessageDto> messagesFromDb90Days = chatHistoryRepository.findMessagesFromDayswrtEmail(dateTime90DaysAgo, email, pageable);

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

    public void storeNewMessageInRedis(String email, Message message) {
        // Store the new message both in Redis and DB
        logger.info("Storing new message in Redis and DB for email: {}", email);
        redisChatHistoryRepository.saveChatHistoryDetails(email, message);
       // chatHistoryRepository.save(message);

        // Asynchronously move the message to DB if needed
        moveMessageToDbAsync(message);
    }

    @Async
    @Transactional
    public void moveMessageToDbAsync(Message message) {
        logger.info("Asynchronously saving message to DB: {}", message);
        chatHistoryRepository.save(message);
    }

    // convert List to Page
    public Page<MessageDto> convertListToPage(List<MessageDto> list, Pageable pageable) {
        int start = Math.min((int) pageable.getOffset(), list.size());
        int end = Math.min((start + pageable.getPageSize()), list.size());

        if (start >= end) {
            return new PageImpl<>(new ArrayList<>(), pageable, list.size());
        }
        List<MessageDto> sublist = list.subList(start, end);
        return new PageImpl<>(sublist, pageable, list.size());
    }
}
