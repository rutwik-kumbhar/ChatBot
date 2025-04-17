package com.monocept.chatbot.reposiotry;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monocept.chatbot.entity.Message;
import com.monocept.chatbot.model.dto.MessageDto;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Repository
public class RedisChatHistoryRepository {
    public static final String HASH_KEY = "chathistory_details";
    private final RedisTemplate<String, Object> redisTemplate;
    private final  ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(RedisChatHistoryRepository.class);

    public RedisChatHistoryRepository(RedisTemplate<String, Object> redisTemplate, ModelMapper modelMapper, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.modelMapper = modelMapper;
        this.objectMapper = objectMapper;
    }

    public  void  saveAll(String email, List<Message> chatHistoryDetails) {
        logger.info("Saving sorted chat history in Redis list for email: {}", email);
        try {

            // Sort descending
           // chatHistoryDetails.sort(Comparator.comparing(Message::getCreatedAt).reversed());

            // Convert to DTOs
            List<MessageDto> chatHistoryDTOs = chatHistoryDetails.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

            // Clear old list if exists , email is the key in redis
            redisTemplate.delete(email);

            // Push all to Redis list
            for (MessageDto dto : chatHistoryDTOs) {
                redisTemplate.opsForList().rightPush(email, dto);
            }
            redisTemplate.expire(email, 3, TimeUnit.DAYS);
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


    public List<MessageDto> getChatHistoryDetailsEmail(String emailId) {
        return new ArrayList<>();
    }

    public void saveChatHistoryDetails(String emailId, List<MessageDto> chatHistoryDetailsEmail) {
    }
}



