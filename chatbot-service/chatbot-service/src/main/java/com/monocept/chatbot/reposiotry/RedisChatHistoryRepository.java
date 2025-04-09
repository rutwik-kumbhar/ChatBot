package com.monocept.chatbot.repository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.monocept.chatbot.Entity.Message;
import com.monocept.chatbot.model.dto.MessageDto;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Repository
public class RedisChatHistoryRepository {
    public static final String HASH_KEY = "chathistory_details";
    private final RedisTemplate<String, Object> redisTemplate;
    private final  ModelMapper modelMapper;
    private static final Logger logger = LoggerFactory.getLogger(RedisChatHistoryRepository.class);

    public RedisChatHistoryRepository(RedisTemplate<String, Object> redisTemplate, ModelMapper modelMapper) {
        this.redisTemplate = redisTemplate;
        this.modelMapper = modelMapper;
    }

    // Save recent chat history details into Redis
    public void saveChatHistoryDetails(String email, Message chatHistoryDetails) {
        logger.info("Saving chat history details in Redis for email: {}", email);
        try {
            String key = email + "_chatHistoryDetails";
            // Convert History object to HistoryDTO (or other relevant object) before saving
            MessageDto chatHistoryDTO = convertToDTO(chatHistoryDetails);
          //  String messgedto = objectMapper.writeValueAsString(chatHistoryDetails);
            redisTemplate.opsForHash().put(HASH_KEY, key, chatHistoryDTO);
            redisTemplate.expire(key, 3, TimeUnit.DAYS);  // Cache for 3 Days
        } catch (Exception ex) {
            logger.error("saveChatHistoryDetails: Exception occurred while saving in Redis: {}", ex.getMessage(), ex);
        }
    }

   private MessageDto convertToDTO(Message chatHistoryDetails) throws JsonProcessingException {
        return modelMapper.map(chatHistoryDetails, MessageDto.class);
    }

    // Get recent chat history details from Redis for a specific email
    public List<MessageDto> getChatHistoryDetailsEmail(String email) {
        String key = email + "_chatHistoryDetails";
        List<MessageDto> histroy=new ArrayList<>();
        try {
            logger.info("Fetching chat history details from Redis for email: {}", email);
            // Retrieve the specific chat history for the given email (key)
            Object chatHistoryRaw = redisTemplate.opsForHash().get(HASH_KEY, key);

            // If data exists for this email
            if (chatHistoryRaw != null) {
                logger.info("Retrieved chat history for email: {}", chatHistoryRaw);
                logger.info("Fetching chat history details from Redis for chatHistoryRaw: {}", chatHistoryRaw.toString());
                MessageDto messageDto = modelMapper.map(chatHistoryRaw, MessageDto.class);
                histroy.add(messageDto);
            }
        } catch (Exception e) {
            // Log the error with the exception details
            logger.error("Error occurred while fetching chat history details from Redis for email: {}", email, e);
            return Collections.emptyList();
        }
        return histroy;
    }
    // Delete chat history from Redis for a specific email (if needed)
    public void deleteChatHistoryDetails(String email) {
        String key = email + "_chatHistoryDetails";
        redisTemplate.opsForHash().delete(HASH_KEY, key);
    }
}
