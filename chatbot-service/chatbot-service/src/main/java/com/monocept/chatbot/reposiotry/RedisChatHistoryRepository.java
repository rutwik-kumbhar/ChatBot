package com.monocept.chatbot.reposiotry;

import com.monocept.chatbot.Entity.Message;
import com.monocept.chatbot.model.dto.MessageDto;
import com.monocept.chatbot.util.RedisUtil;
import com.monocept.chatbot.utils.RedisUtility;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Repository
public class RedisChatHistoryRepository {

    private static final Logger logger = LoggerFactory.getLogger(RedisChatHistoryRepository.class);
    private static final String HASH_KEY = "chathistory_details";

    private final RedisUtility redisUtil;
    private final ModelMapper modelMapper;

    public RedisChatHistoryRepository(RedisUtility redisUtil, ModelMapper modelMapper) {
        this.redisUtil = redisUtil;
        this.modelMapper = modelMapper;
    }

    public void saveChatHistory(String email, Message chatMessage) {
        try {
            logger.info("Saving single chat message in Redis for email: {}", email);
            String key = email + "_latest";
            MessageDto dto = modelMapper.map(chatMessage, MessageDto.class);
            redisUtil.hashPut(HASH_KEY, key, dto);
            redisUtil.expire(key, 3, TimeUnit.DAYS);
        } catch (Exception e) {
            logger.error("Error while saving single message to Redis", e);
        }
    }

    public void saveAllMessages(String email, List<Message> messages) {
        try {
            logger.info("Saving full chat history in Redis for email: {}", email);
            String key = email + "_full";

            List<MessageDto> dtoList = messages.stream()
                    .map(msg -> modelMapper.map(msg, MessageDto.class))
                    .collect(Collectors.toList());

            //redisUtil.listDelete(key); // clear old
            //dtoList.forEach(dto -> redisUtil.listRightPush(key, dto));
            //redisUtil.expire(key, 3, TimeUnit.DAYS);
        } catch (Exception e) {
            logger.error("Error while saving chat history list to Redis", e);
        }
    }

    public List<MessageDto> getLatestMessage(String email) {
        try {
            String key = email + "_latest";
            Object obj = redisUtil.hashGet(HASH_KEY, key);
            if (obj != null) {
                return Collections.singletonList(modelMapper.map(obj, MessageDto.class));
            }
        } catch (Exception e) {
            logger.error("Error while retrieving latest message from Redis", e);
        }
        return Collections.emptyList();
    }

    public List<MessageDto> getFullHistory(String email) {
        try {
            String key = email + "_full";
            List<Object> rawList = redisUtil.listRange(key, 0, -1);
            if (rawList == null || rawList.isEmpty()) return Collections.emptyList();

            List<MessageDto> history = new ArrayList<>();
            for (Object obj : rawList) {
                history.add(modelMapper.map(obj, MessageDto.class));
            }
            return history;
        } catch (Exception e) {
            logger.error("Error while retrieving full chat history from Redis", e);
            return Collections.emptyList();
        }
    }

    public void deleteHistory(String email) {
        redisUtil.hashDelete(HASH_KEY, email + "_latest");
        redisUtil.delete(email + "_full");
    }
}

 