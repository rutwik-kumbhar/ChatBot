package com.monocept.chatbot.reposiotry;

import com.monocept.chatbot.Entity.History;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Repository

public class RedisChatHistoryRepository {
    public static final String HASH_KEY = "chathistory_details";
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChatHistoryRepository chatHistoryRepository;

    public RedisChatHistoryRepository(RedisTemplate<String, Object> redisTemplate, ChatHistoryRepository chatHistoryRepository) {
        this.redisTemplate = redisTemplate;
        this.chatHistoryRepository = chatHistoryRepository;
    }

    // Save recent chat history details into Redis
    public void saveChatHistoryDetails(String email, History chatHistoryDetails) {
        log.info("Saving chat history details in Redis for email: {}", email);
        String key = email + "_chatHistoryDetails";
        redisTemplate.opsForHash().put(HASH_KEY, key, chatHistoryDetails);
        redisTemplate.expire(HASH_KEY, 3, TimeUnit.DAYS);  // Cache for 1 hour
    }

    // Get recent chat history details from Redis
    public Object getChatHistoryDetails(String email) {
        String key = email + "_chatHistoryDetails";
        return redisTemplate.opsForHash().get(HASH_KEY, key);
    }

    // Delete chat history from Redis (if needed)
    public void deleteChatHistoryDetails(String email) {
        String key = email + "_chatHistoryDetails";
        redisTemplate.opsForHash().delete(HASH_KEY, key);
    }

    public void moveOldMessagesToDb(String email) {
        String key = email + "_chatHistoryDetails";
        LocalDateTime threeDaysAgo = LocalDateTime.now().minus(3, ChronoUnit.DAYS);

        // Fetch all messages in Redis
        List<Object> redisMessages = redisTemplate.opsForList().range(key, 0, -1);

        if (redisMessages != null && !redisMessages.isEmpty()) {
            List<History> messagesToMoveToDb = redisMessages.stream()
                    .map(obj -> (History) obj)
                    .filter(message -> message.getDateTime().isBefore(threeDaysAgo))
                    .collect(Collectors.toList());

            List<History> remainingMessages = redisMessages.stream()
                    .map(obj -> (History) obj)
                    .filter(message -> !message.getDateTime().isBefore(threeDaysAgo))
                    .collect(Collectors.toList());
            // Save 3 days older messages to the Database
            if (!messagesToMoveToDb.isEmpty()) {
                chatHistoryRepository.saveAll(messagesToMoveToDb);
            }

            // Update the Redis list with the remaining messages (less than 3 days old)
            redisTemplate.opsForList().leftPushAll(key, remainingMessages);
        }
    }

}
