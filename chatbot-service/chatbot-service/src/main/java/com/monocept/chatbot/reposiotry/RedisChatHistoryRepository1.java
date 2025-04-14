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
import java.util.stream.Collectors;

@Repository
public class RedisChatHistoryRepository1 {
    public static final String HASH_KEY = "chathistory_details";
    private final RedisTemplate<String, Object> redisTemplate;
    private final  ModelMapper modelMapper;
    private static final Logger logger = LoggerFactory.getLogger(com.monocept.chatbot.repository.RedisChatHistoryRepository1.class);

    public RedisChatHistoryRepository1(RedisTemplate<String, Object> redisTemplate, ModelMapper modelMapper) {
        this.redisTemplate = redisTemplate;
        this.modelMapper = modelMapper;
    }

    // Save recent chat history details into Redis
    public void saveChatHistoryDetails(String email, Message chatHistoryDetails) {
        logger.info("Saving chat history details in Redis for email: {}", email);
        try {
            String key = email + "_newchatHistoryDetails";
            // Convert History object to HistoryDTO (or other relevant object) before saving
            MessageDto chatHistoryDTO = convertToDTO1(chatHistoryDetails);
          //  String messgedto = objectMapper.writeValueAsString(chatHistoryDetails);
            redisTemplate.opsForHash().put(HASH_KEY, key, chatHistoryDTO);
            redisTemplate.expire(key, 3, TimeUnit.DAYS);  // Cache for 3 Days
        } catch (Exception ex) {
            logger.error("saveChatHistoryDetails: Exception occurred while saving in Redis: {}", ex.getMessage(), ex);
        }
    }

    public  void  saveAll(String email, List<Message> chatHistoryDetails) {
        logger.info("Saving sorted chat history in Redis list for email: {}", email);
        try {
            String key = email;

            // Sort descending
           // chatHistoryDetails.sort(Comparator.comparing(Message::getCreatedAt).reversed());

            // Convert to DTOs
            List<MessageDto> chatHistoryDTOs = chatHistoryDetails.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

            // Clear old list if exists
            redisTemplate.delete(key);

            // Push all to Redis list
            for (MessageDto dto : chatHistoryDTOs) {
                redisTemplate.opsForList().rightPush(key, dto);
            }
            redisTemplate.expire(key, 3, TimeUnit.DAYS);
        } catch (Exception ex) {
            logger.error("Exception while saving chat history in Redis List: {}", ex.getMessage(), ex);
        }
    }

    public MessageDto convertToDto(Message message) {
        MessageDto dto = new MessageDto();
        dto.setId(message.getId());
        dto.setUserId(message.getUserId());
        dto.setEmail(message.getEmail());
        dto.setSendType(message.getSendType());
        dto.setMessageType(message.getMessageType());
        dto.setMessageId(message.getMessageId());
        dto.setMessageTo(message.getMessageTo());
        dto.setText(message.getText());
        dto.setReplyToMessageId(message.getReplyToMessageId());
        dto.setStatus(message.getStatus());
        dto.setEmoji(message.getEmoji());
        dto.setAction(message.getAction());
        dto.setMedia(message.getMedia());
        dto.setOptions(message.getOptions());
        dto.setBotOptions(message.isBotOptions());
        dto.setPlatform(message.getPlatform());
        dto.setCreatedAt(message.getCreatedAt());
        return dto;
    }
    private MessageDto convertToDTO1(Message chatHistoryDetails) throws JsonProcessingException {
        return modelMapper.map(chatHistoryDetails, MessageDto.class);
    }

    // Get recent chat history details from Redis for a specific email
    public List<MessageDto> getChatHistoryDetailsEmail1(String email) {
        String key = email + "_newchatHistoryDetails";
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

    public List<MessageDto> getChatHistoryDetailsEmail(String email) {
        String key = email;
        List<MessageDto> history = new ArrayList<>();

        try {
            logger.info("Fetching chat history details from Redis for email: {}", email);

            Object chatHistoryRaw = redisTemplate.opsForHash().get(HASH_KEY, key);

            if (chatHistoryRaw != null) {
                logger.info("Retrieved chat history from Redis: {}", chatHistoryRaw);

                // Cast or convert to list
                if (chatHistoryRaw instanceof List<?>) {
                    List<?> rawList = (List<?>) chatHistoryRaw;

                    for (Object obj : rawList) {
                        // Convert each entry to MessageDto
                        MessageDto dto = modelMapper.map(obj, MessageDto.class);
                        history.add(dto);
                    }
                } else {
                    logger.warn("Redis data is not a list for key: {}", key);
                }
            }
        } catch (Exception e) {
            logger.error("Error fetching chat history for email: {}", email, e);
            return Collections.emptyList();
        }

        return history;
    }


    // Delete chat history from Redis for a specific email (if needed)
    public void deleteChatHistoryDetails(String email) {
        String key = email + "_chatHistoryDetails";
        redisTemplate.opsForHash().delete(HASH_KEY, key);
    }
}
