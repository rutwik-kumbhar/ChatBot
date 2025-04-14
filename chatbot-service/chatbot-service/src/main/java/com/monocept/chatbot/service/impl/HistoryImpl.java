package com.monocept.chatbot.service.impl;

import com.monocept.chatbot.Entity.Message;
import com.monocept.chatbot.exceptions.ResourcesNotFoundException;
import com.monocept.chatbot.model.dto.MessageDto;
import com.monocept.chatbot.reposiotry.ChatHistoryRepository;
import com.monocept.chatbot.service.ChatHistoryService;
import com.monocept.chatbot.utils.RedisUtility1;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class HistoryImpl implements ChatHistoryService {

    @Autowired
    private ModelMapper modelMapper;
    private static final Logger logger = LoggerFactory.getLogger(ChatHistoryServiceImpl.class);
    private  final RedisUtility1 redisUtility;
    private final ChatHistoryRepository chatHistoryRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final com.monocept.chatbot.repository.RedisChatHistoryRepository1 redisChatHistoryRepository;

    public HistoryImpl(RedisUtility1 redisUtility, ChatHistoryRepository chatHistoryRepository, RedisTemplate<String, Object> redisTemplate, com.monocept.chatbot.repository.RedisChatHistoryRepository1 redisChatHistoryRepository) {
        this.redisUtility = redisUtility;
        this.chatHistoryRepository = chatHistoryRepository;
        this.redisTemplate = redisTemplate;
        this.redisChatHistoryRepository = redisChatHistoryRepository;
    }

    public Page<MessageDto> getChatHistory(String email, int page, int size) {
        // 1) Try Redis firs
        //
        List<MessageDto> recentFromRedis = redisChatHistoryRepository.getChatHistoryDetailsEmail(email);
        logger.info("get message from redis {}:");
        if (recentFromRedis != null && !recentFromRedis.isEmpty()) {
            // manual pagination
             //page  = pageable.getPageNumber();
            //int size  = pageable.getPageSize();
            Pageable pageable = PageRequest.of(page, size);
            int start = page * size;
            int end   = Math.min(start + size, recentFromRedis.size());

            if (start < recentFromRedis.size()) {
                List<MessageDto> slice = recentFromRedis.subList(start, end);
                return new PageImpl<>(slice, pageable, recentFromRedis.size());
            }
        }

        // 2) Fallback: Redis empty or out of range → load from DB
       Page<MessageDto> dbPage = getMessagesFromDB(email, page, size);
        System.out.println("db output:"+dbPage);
        if (dbPage.isEmpty()) {
            throw new ResourcesNotFoundException(
                    "No messages found for the provided email in the last 3 days.");
        }

        // 3) Optionally cache this page in Redis for next time

        redisUtility.saveDataForLast3DaysToRedis(convertToEntityList(dbPage.getContent()),email);

        return dbPage;
       // return null;
    }
    public List<Message> convertToEntityList(List<MessageDto> dtoList) {
        return dtoList.stream()
                .map(dto -> {
                    Message message = modelMapper.map(dto, Message.class);
                    message.setUpdatedAt(null);
                    message.setCreatedAt(null);
                    // Set current time or any custom logic
                    return message;
                })
                .collect(Collectors.toList());
    }


    @Override
    public Page<MessageDto> getMessagesFromLast90Days(String email, int page, int size) {
        return null;
    }

    public Page<MessageDto> getMessagesFromDB(String email, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Message> messagePage = chatHistoryRepository.findByEmail(email, pageable);
        return messagePage.map(this::convertToDto);
    }

    private  MessageDto convertToDto(Message message) {
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
