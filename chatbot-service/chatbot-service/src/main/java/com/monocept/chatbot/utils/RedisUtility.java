package com.monocept.chatbot.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monocept.chatbot.Entity.Message;
import com.monocept.chatbot.model.dto.MessageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class RedisUtility {
    private static final Logger logger = LoggerFactory.getLogger(RedisUtility.class);

    private final ObjectMapper objectMapper;
    private final RedisTemplate<String,String> redisTemplate;

    public RedisUtility(ObjectMapper objectMapper, RedisTemplate<String, String> redisTemplate) {
        this.objectMapper = objectMapper;
        this.redisTemplate = redisTemplate;
    }
    public static void saveDataForLast3DaysToRedis(List<MessageDto> messages, String email) {
        try {
             ///redisChatHistoryRepository.saveAll(messages);
           // logger.info("Cached {} messages in Redis for {}", messages.size(), email);
        } catch (Exception e) {
            //logger.error("Error caching messages in Redis for {}: {}", email, e.getMessage());
        }
    }
    public Page<MessageDto> getPaginatedMessagesFromRedis(String email, Pageable pageable) {
        String key = email + ":chatMessagesSorted";

        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();

        long start = (long) page * size;
        long end = start + size - 1;

        // Get messages in reverse (newest first)
        Set<String> rawMessages = redisTemplate.opsForZSet().reverseRange(key, start, end);

        if (rawMessages == null || rawMessages.isEmpty()) {
            return Page.empty(pageable);
        }

        List<MessageDto> messages = rawMessages.stream()
                .map(json -> {
                    try {
                        return objectMapper.readValue(json, MessageDto.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("Deserialization failed", e);
                    }
                })
                .collect(Collectors.toList());

        Long total = redisTemplate.opsForZSet().zCard(key);

        return new PageImpl<>(messages, pageable, total == null ? 0 : total);
    }
    public void saveMessagesToRedisSortedSet(String email, List<Message> messages) {
        String key = email + ":chatMessagesSorted";

        try {
            for (Message msg : messages) {
                MessageDto dto = convertToDto(msg);
                String json = objectMapper.writeValueAsString(dto);

                // Use message creation timestamp as score
                double score = msg.getCreatedAt().toInstant().toEpochMilli();
                redisTemplate.opsForZSet().add(key, json, score);
            }

            // Set TTL: 3 days
            redisTemplate.expire(key, 3, TimeUnit.DAYS);

            logger.info("Saved {} messages to Redis sorted set for {}", messages.size(), email);
        } catch (Exception e) {
            logger.error("Error saving messages to Redis for {}: {}", email, e.getMessage(), e);
        }
    }
    private static MessageDto convertToDto(Message message) {
        return new MessageDto(
                message.getId(),
                message.getUserId(),
                message.getEmail(),
                message.getSendType(),
                message.getMessageType(),
                message.getMessageId(),
                message.getMessageTo(),
                message.getText(),
                message.getReplyToMessageId(),
                message.getStatus(),
                message.getEmoji(),
                message.getAction(),
                message.getMedia(),
                message.getOptions(),
                message.isBotOptions(),
                message.getPlatform(),
                message.getCreatedAt()
        );

    }
    }
