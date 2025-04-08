package com.monocept.chatbot.repository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.stream.Collectors;

@Repository
public class RedisChatHistoryRepository {
    public static final String HASH_KEY = "chathistory_details";
    private final RedisTemplate<String, Object> redisTemplate;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(RedisChatHistoryRepository.class);


    public RedisChatHistoryRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Save recent chat history details into Redis
    public void saveChatHistoryDetails(String email, Message chatHistoryDetails) {
        logger.info("Saving chat history details in Redis for email: {}", email);
        try {
            String key = email + "_chatHistoryDetails";
            // Convert History object to HistoryDTO (or other relevant object) before saving
            MessageDto chatHistoryDTO = convertToDTO(chatHistoryDetails);
            redisTemplate.opsForHash().put(HASH_KEY, key, chatHistoryDTO);
            redisTemplate.expire(key, 2, TimeUnit.MINUTES);  // Cache for 3 Days
        } catch (Exception ex) {
            logger.error("saveChatHistoryDetails: Exception occurred while saving in Redis: {}", ex.getMessage(), ex);
        }
    }
    // Convert History entity to DTO
  /*  private MessageDto convertToDTO(MessageDto chatHistoryDetails) {
        //  this can be customized as per your logic
        return new MessageDto(chatHistoryDetails.getId(),chatHistoryDetails.getUserId(),chatHistoryDetails.getEmail(),chatHistoryDetails.getSendType(),chatHistoryDetails.getMessageType(),chatHistoryDetails.getMessageId(),chatHistoryDetails.getMessageTo(),chatHistoryDetails.getText(),chatHistoryDetails.getReplyToMessageId(),chatHistoryDetails.getStatus(),chatHistoryDetails.getEmoji(),chatHistoryDetails.getAction(),chatHistoryDetails.getAction(),
                chatHistoryDetails.getMedia(),chatHistoryDetails.getBotOptions(),chatHistoryDetails.getBotOptions(),chatHistoryDetails.getPlatform(),chatHistoryDetails.getCreatedAt());
       // return new HistoryDTO(chatHistoryDetails.getMsgId(), chatHistoryDetails.getMsg(),
         //     chatHistoryDetails.getMessageTo(), chatHistoryDetails.getDateTime(), chatHistoryDetails.getReplyId(),chatHistoryDetails.getType(),chatHistoryDetails.getMediaUrl(),chatHistoryDetails.getActivity());
    }*/
    private MessageDto convertToDTO(Message chatHistoryDetails) {
        ObjectMapper objectMapper = new ObjectMapper();
        // Use ObjectMapper to map Message to MessageDto
        return objectMapper.convertValue(chatHistoryDetails, MessageDto.class);
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
                ModelMapper modelMapper = new ModelMapper();
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
