package com.monocept.chatbot.repository;
import com.monocept.chatbot.Entity.History;
import com.monocept.chatbot.model.dto.HistoryDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Repository
public class RedisChatHistoryRepository {
    public static final String HASH_KEY = "chathistory_details";
    private final RedisTemplate<String, Object> redisTemplate;
    private static final Logger logger = LoggerFactory.getLogger(RedisChatHistoryRepository.class);


    public RedisChatHistoryRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Save recent chat history details into Redis
    public void saveChatHistoryDetails(String email, History chatHistoryDetails) {
        logger.info("Saving chat history details in Redis for email: {}", email);
        try {
            String key = email + "_chatHistoryDetails";

            // Convert History object to HistoryDTO (or other relevant object) before saving
            HistoryDTO chatHistoryDTO = convertToDTO(chatHistoryDetails);
            redisTemplate.opsForHash().put(HASH_KEY, key, chatHistoryDTO);
            redisTemplate.expire(key, 3, TimeUnit.DAYS);  // Cache for 3 Days
        } catch (Exception ex) {
            logger.error("saveChatHistoryDetails: Exception occurred while saving in Redis: {}", ex.getMessage(), ex);
        }
    }

    // Convert History entity to DTO (or map it as needed)
    private HistoryDTO convertToDTO(History chatHistoryDetails) {
        // Assuming a simple conversion, this can be customized as per your logic
        return new HistoryDTO(chatHistoryDetails.getMsgId(), chatHistoryDetails.getMsg(),
              chatHistoryDetails.getMessageTo(), chatHistoryDetails.getDateTime(), chatHistoryDetails.getReplyId(),chatHistoryDetails.getType(),chatHistoryDetails.getMediaUrl(),chatHistoryDetails.getActivity());
    }

    // Get recent chat history details from Redis for a specific email
    public List<HistoryDTO> getChatHistoryDetailsEmail(String email) {
        String key = email + "_chatHistoryDetails";
        // Retrieve the chat history list from the hash
        List<Object> rawData = redisTemplate.opsForHash().values(HASH_KEY);

        // Filter and convert to the expected type (HistoryDTO)
        return rawData.stream()
                .filter(HistoryDTO.class::isInstance)
                .map(HistoryDTO.class::cast)
                .collect(Collectors.toList());
    }

    // Delete chat history from Redis for a specific email (if needed)
    public void deleteChatHistoryDetails(String email) {
        String key = email + "_chatHistoryDetails";
        redisTemplate.opsForHash().delete(HASH_KEY, key);
    }
}
