/*package com.monocept.chatbot.service;

import com.monocept.chatbot.model.dto.MessageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

   // @Autowired
    private final RedisTemplate<String, MessageDto> redisTemplate;

    private static final String REDIS_KEY_PREFIX = "chathistory_details";

    public MessageService(RedisTemplate<String, MessageDto> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Method to fetch paginated messages for a given emailId from Redis.
     *
     * @param emailId The email ID for which we want to fetch messages.
     * @param pageNo  The page number to fetch (zero-based index).
     * @param limit   The number of messages per page.
     * @return A list of MessageDto objects for the requested page.
     */
  /*  public List<MessageDto> getPaginatedMessages(String emailId, int pageNo, int limit) {
        // Calculate the starting and ending index for pagination
        int start = pageNo * limit;
        int end = start + limit - 1;

        // Fetch messages from Redis list using LRANGE
        // The key in Redis will be "message:<emailId>"
        List<MessageDto> messages = redisTemplate.opsForList().range(REDIS_KEY_PREFIX +  emailId+"_chatHistoryDetails", start, end);

        return messages;
    }
}*/