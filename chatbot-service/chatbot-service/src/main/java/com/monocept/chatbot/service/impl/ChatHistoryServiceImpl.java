package com.monocept.chatbot.service.impl;

import com.monocept.chatbot.Entity.History;
import com.monocept.chatbot.exceptions.ResourcesNotFoundException;
import com.monocept.chatbot.model.dto.HistoryDTO;
import com.monocept.chatbot.Exception.InvalidEmailException;
import com.monocept.chatbot.reposiotry.ChatHistoryRepository;
import com.monocept.chatbot.reposiotry.RedisChatHistoryRepository;
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
import java.util.concurrent.TimeUnit;

@Service
public class ChatHistoryServiceImpl implements ChatHistoryService {

    private final ChatHistoryRepository chatHistoryRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisChatHistoryRepository redisChatHistoryRepository;
    private static final String REDIS_KEY_PREFIX = "chat_history:";

    public ChatHistoryServiceImpl(ChatHistoryRepository chatHistoryRepository, RedisTemplate<String, Object> redisTemplate, RedisChatHistoryRepository redisChatHistoryRepository) {
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

        History newMessage = new History();
        newMessage.setMsgId(8L);
        newMessage.setMsg("Hi Ely");
        newMessage.setMessageTo("user");
        newMessage.setDateTime(null);
        newMessage.setReplyId(null);
        newMessage.setType("text");
        newMessage.setMediaUrl(null);
        newMessage.setActivity("like");
        newMessage.setSsoid(null);
        newMessage.setAgentId(null);
        newMessage.setEmail(email);
        newMessage.setSession(null);
        storeNewMessageInRedis(email, newMessage);

        // Get the 3-day chat history from Redis
        LocalDateTime dateTime3DaysAgo = LocalDateTime.now().minus(3, ChronoUnit.DAYS);
        String redisKey = REDIS_KEY_PREFIX + email + ":last3days";

        // Fetch from Redis for the last 3 days
        Page<HistoryDTO> recentMessagesFromRedis = (Page<HistoryDTO>) redisTemplate.opsForValue().get(redisKey);

        List<HistoryDTO> finalMessages = new ArrayList<>();

        // If we have messages in Redis (recent 3 days), add them to the response
        if (recentMessagesFromRedis != null && !recentMessagesFromRedis.isEmpty()) {
            finalMessages.addAll(recentMessagesFromRedis.getContent());
        } else {
            // If not found in Redis, fetch it from the database for the last 3 days
            Pageable pageableFor3Days = PageRequest.of(0, size);
            Page<HistoryDTO> messagesFromDb3Days = chatHistoryRepository.findMessagesFromDayswrtEmail(dateTime3DaysAgo, email, pageableFor3Days);
            if (!messagesFromDb3Days.isEmpty()) {
                finalMessages.addAll(messagesFromDb3Days.getContent());

                // Cache the recent 3 days' messages in Redis with a TTL of 1 hour
              //  redisChatHistoryRepository.saveChatHistoryDetails(email,finalMessages);
            //    redisTemplate.opsForValue().set(redisKey, messagesFromDb3Days, 1, TimeUnit.HOURS);
            }
        }

        //  fetch the remaining messages for the next 87 days from the database
        LocalDateTime dateTime87DaysAgo = LocalDateTime.now().minus(87, ChronoUnit.DAYS);
        Pageable pageableFor87Days = PageRequest.of(page, size);
        Page<HistoryDTO> messagesFromDb87Days = chatHistoryRepository.findMessagesFromDayswrtEmail(dateTime87DaysAgo, email, pageableFor87Days);

        if (messagesFromDb87Days.isEmpty() && finalMessages.isEmpty()) {
            throw new ResourcesNotFoundException("No messages found for the provided email in the last 90 days.");
        }

        // Add 87 days' data to the final list (if available)
        finalMessages.addAll(messagesFromDb87Days.getContent());

        // Convert final list back to a Page if required
        return new PageImpl<>(finalMessages, pageableFor87Days, messagesFromDb87Days.getTotalElements());
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
       // redisTemplate.opsForList().leftPush(redisKey, newMessage); // Store new message at the start of the list in Redis
        // After saving the message in Redis, move older messages to the database
        redisChatHistoryRepository.moveOldMessagesToDb(email);
    }


}
