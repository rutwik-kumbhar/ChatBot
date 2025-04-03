package com.monocept.chatbot.repository;
import com.monocept.chatbot.Entity.History;
import com.monocept.chatbot.model.dto.HistoryDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
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
  /*  public List<HistoryDTO> getChatHistoryDetailsEmail(String email) {
        String key = email + "_chatHistoryDetails";
        // Retrieve the chat history list from the hash
        List<Object> rawData = redisTemplate.opsForHash().values(HASH_KEY);

        // Filter and convert to the expected type (HistoryDTO)
        return rawData.stream()
                .filter(HistoryDTO.class::isInstance)
                .map(HistoryDTO.class::cast)
                .collect(Collectors.toList());
    }*/
    /*public List<HistoryDTO> getChatHistoryDetailsEmail(String email) {
        String key = email + "_chatHistoryDetails";
        try {
            // Log the start of the operation
            logger.info("Fetching chat history details for email: {}", email);
            // Retrieve the chat history list from the hash
            List<Object> rawData = redisTemplate.opsForHash().values(HASH_KEY);
            // Log the retrieved data size (optional)
            logger.debug("Retrieved {} raw data entries for email: {}", rawData.size(), email);
            // Filter and convert to the expected type (HistoryDTO)
            List<HistoryDTO> chatHistoryDetails = rawData.stream()
                    .filter(HistoryDTO.class::isInstance)
                    .map(HistoryDTO.class::cast)
                    .collect(Collectors.toList());
            // Log the number of successfully converted HistoryDTO objects
            logger.debug("Successfully converted {} entries to HistoryDTO for email: {}", chatHistoryDetails.size(), email);
            // Log the successful completion of the operation
            logger.info("Chat history details fetched successfully for email: {}", email);
            return chatHistoryDetails;
        } catch (Exception e) {
            // Log the error with the exception details
            logger.error("Error occurred while fetching chat history details for email: {}", email, e);
            return Collections.emptyList();
        }
    }*/
    public List<HistoryDTO> getChatHistoryDetailsEmail(String email) {
        String key = email + "_chatHistoryDetails";
        try {
            // Log the start of the operation
            logger.info("Fetching chat history details for email: {}", email);

            // Retrieve the chat history list from the hash
            List<Object> rawData = redisTemplate.opsForHash().values(HASH_KEY);

            // Log the retrieved data size (optional)
            logger.debug("Retrieved {} raw data entries for email: {}", rawData.size(), email);

            // Filter and convert to the expected type (HistoryDTO)
            List<HistoryDTO> chatHistoryDetails = rawData.stream()
                    .filter(HistoryDTO.class::isInstance)
                    .map(HistoryDTO.class::cast)
                    .collect(Collectors.toList());

            // Log the number of successfully converted HistoryDTO objects
            logger.debug("Successfully converted {} entries to HistoryDTO for email: {}", chatHistoryDetails.size(), email);

            // Log the chat history details (with a size limit to avoid excessive logging)
            if (chatHistoryDetails.size() > 0) {
                logger.debug("Chat history details for email {}: {}", email, chatHistoryDetails.subList(0, Math.min(5, chatHistoryDetails.size())));  // Logs up to 5 entries for brevity
            } else {
                logger.debug("No chat history details found for email: {}", email);
            }

            // Log the successful completion of the operation
            logger.info("Chat history details fetched successfully for email: {}", email);
            return chatHistoryDetails;
        } catch (Exception e) {
            // Log the error with the exception details
            logger.error("Error occurred while fetching chat history details for email: {}", email, e);
            return Collections.emptyList();
        }
    }




    // Delete chat history from Redis for a specific email (if needed)
    public void deleteChatHistoryDetails(String email) {
        String key = email + "_chatHistoryDetails";
        redisTemplate.opsForHash().delete(HASH_KEY, key);
    }
}
