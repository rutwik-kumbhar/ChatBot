package com.monocept.chatbot.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monocept.chatbot.entity.Message;
import com.monocept.chatbot.model.dto.MessageDto;
import com.monocept.chatbot.reposiotry.ChatHistoryRepository;
import com.monocept.chatbot.reposiotry.RedisChatHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtility {

    private static final Logger logger = LoggerFactory.getLogger(RedisUtility.class);
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, String> redisTemplate;
    private final RedisChatHistoryRepository redisChatHistoryRepository;
    private final ChatHistoryRepository chatHistoryRepository;
    private final String redisSuffix=":chatMessagesSorted";
    private final int redisTTL=3;

    public RedisUtility(ObjectMapper objectMapper, RedisTemplate<String, String> redisTemplate, RedisChatHistoryRepository redisChatHistoryRepository, ChatHistoryRepository chatHistoryRepository) {
        this.objectMapper = objectMapper;
        this.redisTemplate = redisTemplate;
        this.redisChatHistoryRepository = redisChatHistoryRepository;
        this.chatHistoryRepository = chatHistoryRepository;
    }

    public void saveLatestDataToRedis(List<Message> messages, String email) {
        try {
            redisChatHistoryRepository.saveAll(email, messages);
            logger.info("Cached {} messages in Redis for {}", messages.size(), email);
        } catch (Exception e) {
            logger.error("Error caching messages in Redis for {}: {}", email, e.getMessage());
        }
    }

    public Page<MessageDto> getPaginatedMessagesFromRedis(String email, Pageable pageable) {
        String key = email + redisSuffix;
        long start = (long) pageable.getPageNumber() * pageable.getPageSize();
        long end = start + pageable.getPageSize() - 1;

        // Step 1: Try fetching from Redis
        Set<String> rawMessages = redisTemplate.opsForZSet().reverseRange(key, start, end);

        if (!rawMessages.isEmpty()) {
            logger.info("Fetched {} messages from Redis for {}", rawMessages.size(), email);

            List<MessageDto> messages = rawMessages.stream()
                    .map(this::safeDeserialize)
                    .filter(Objects::nonNull).toList();


            Long totalCount = redisTemplate.opsForZSet().zCard(key);
            return new PageImpl<>(messages, pageable, totalCount == null ? 0 : totalCount);
        }

        // Step 2: If Redis is empty, fallback to DB
        logger.info("No chat history in Redis for {}. Fetching from DB...", email);
        Page<Message> dbMessages =   chatHistoryRepository.findByEmail(email,pageable);

        if (dbMessages == null || dbMessages.isEmpty()) {
            return Page.empty(pageable);
        }

        // Step 3: Cache DB messages to Redis sorted set
        saveMessagesToRedisSortedSet(email, dbMessages.getContent());

        // Step 4: Convert to DTO and return
        List<MessageDto> dtoList = dbMessages.getContent().stream()
                .map(RedisUtility::convertToDto).toList();


        return new PageImpl<>(dtoList, pageable, dbMessages.getTotalElements());
    }

    private MessageDto safeDeserialize(String json) {
        try {
            return objectMapper.readValue(json, MessageDto.class);
        } catch (JsonProcessingException e) {
            logger.error("Error deserializing message from Redis: {}", e.getMessage());
            return null;
        }
    }

    public void saveMessagesToRedisSortedSet(String email, List<Message> messages) {
        String key = email +redisSuffix;
        try {
            for (Message msg : messages) {
                MessageDto dto = convertToDto(msg);
                String json = objectMapper.writeValueAsString(dto);
                // Use message creation timestamp as score
                double score = msg.getCreatedAt().toInstant().toEpochMilli();
                redisTemplate.opsForZSet().add(key, json, score);
            }
            // Set TTL: 3 days
            redisTemplate.expire(key, redisTTL, TimeUnit.DAYS);
            logger.info("Saved {} messages to Redis sorted set for {}", messages.size(), email);
        } catch (Exception e) {
            logger.error("Error saving messages to Redis for {}: {}", email, e.getMessage(), e);
        }
    }

    public void saveMessageToRedisSortedSet(String email, Message msg) {
        String key = email +redisSuffix;
        try {

                MessageDto dto = convertToDto(msg);
                String json = objectMapper.writeValueAsString(dto);
                // Use message creation timestamp as score
                double score = msg.getCreatedAt().toInstant().toEpochMilli();
                redisTemplate.opsForZSet().add(key, json, score);

            // Set TTL: 3 days
            redisTemplate.expire(key, redisTTL, TimeUnit.DAYS);
            logger.info("Saved {} message to Redis sorted set for {}", msg, email);
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
                MediaDtoConverter.convertToEntityAttribute(message.getMedia()),
                message.getOptions(),
                message.isBotOptions(),
                message.getPlatform(),
                message.getCreatedAt()
        );
    }
}

 