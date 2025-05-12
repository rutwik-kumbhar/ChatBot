package com.monocept.chatbot.service.impl;

import com.monocept.chatbot.entity.Message;
import com.monocept.chatbot.model.dto.MessageDto;
import com.monocept.chatbot.reposiotry.ChatHistoryRepository;
import com.monocept.chatbot.reposiotry.RedisChatHistoryRepository;
import com.monocept.chatbot.service.ChatHistoryService;
import com.monocept.chatbot.utils.MediaDtoConverter;
import com.monocept.chatbot.utils.RedisUtility;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatHistoryServiceImpl implements ChatHistoryService {

    private static final Logger logger = LoggerFactory.getLogger(ChatHistoryServiceImpl.class);

    private final RedisUtility redisUtility;
    private final ChatHistoryRepository chatHistoryRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisChatHistoryRepository redisChatHistoryRepository;
    private ModelMapper modelMapper;

    public ChatHistoryServiceImpl(ModelMapper modelMapper, RedisChatHistoryRepository redisChatHistoryRepository, RedisTemplate<String, Object> redisTemplate, ChatHistoryRepository chatHistoryRepository, RedisUtility redisUtility) {
        this.modelMapper = modelMapper;
        this.redisChatHistoryRepository = redisChatHistoryRepository;
        this.redisTemplate = redisTemplate;
        this.chatHistoryRepository = chatHistoryRepository;
        this.redisUtility = redisUtility;
    }

    public Page<MessageDto> getChatHistory(String agentId, int page, int size) {
        logger.info("Fetching chat history (new) for agentID: {}, page: {}, size: {}", agentId, page, size);
        Pageable pageable = PageRequest.of(page, size);
        return redisUtility.getPaginatedMessagesFromRedis(agentId, pageable);
    }


    public Page<MessageDto> getMessagesFromDB(String agentId, int page, int size) {
        logger.debug("Querying database for email: {}, page: {}, size: {}", agentId, page, size);
        Pageable pageable = PageRequest.of(page, size);
        //Page<Message> messagePage = chatHistoryRepository.findByEmail(email, pageable);
        Page<Message> messagePage = chatHistoryRepository.findByUserId(agentId, pageable);
        logger.info("Database query returned {} records for agentId: {}", messagePage.getContent().size(), agentId);
        return messagePage.map(this::convertToDto);
    }

    private MessageDto convertToDto(Message message) {
        logger.debug("Converting message entity to DTO: {}", message.getId());
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