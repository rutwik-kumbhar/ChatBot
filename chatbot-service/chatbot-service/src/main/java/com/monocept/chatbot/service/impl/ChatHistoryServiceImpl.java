package com.monocept.chatbot.service.impl;
import com.monocept.chatbot.Entity.Message;
import com.monocept.chatbot.enums.*;
import com.monocept.chatbot.exceptions.ResourcesNotFoundException;
import com.monocept.chatbot.model.dto.MediaDto;
import com.monocept.chatbot.model.dto.MessageDto;
import com.monocept.chatbot.reposiotry.ChatHistoryRepository;
import com.monocept.chatbot.service.ChatHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatHistoryServiceImpl implements ChatHistoryService {
    private static final Logger logger = LoggerFactory.getLogger(ChatHistoryServiceImpl.class);

    private final ChatHistoryRepository chatHistoryRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final com.monocept.chatbot.repository.RedisChatHistoryRepository redisChatHistoryRepository;

    public ChatHistoryServiceImpl(ChatHistoryRepository chatHistoryRepository,
                                  RedisTemplate<String, Object> redisTemplate,
                                  com.monocept.chatbot.repository.RedisChatHistoryRepository redisChatHistoryRepository) {
        this.chatHistoryRepository = chatHistoryRepository;
        this.redisTemplate = redisTemplate;
        this.redisChatHistoryRepository = redisChatHistoryRepository;
    }

    @Override

    public Page<MessageDto> getMessagesFromLast90Days(String email, int page, int size) {
        Message message = new Message();
        message.setId(5L);
        message.setUserId("manish123");
        message.setEmail("user123@gmail.com");
        message.setSendType(MessageSendType.MESSAGE);
        message.setMessageType(MessageType.TEXT);
        message.setMessageId("msg-001");
        message.setMessageTo(MessageTo.USER);
        message.setText("Hello,how are you.");
        message.setReplyToMessageId("msg-0001");
        message.setStatus(MessageStatus.READ);
        message.setEmoji("\uD83D\uDE0A");
        message.setAction(Action.SELECTED);
        message.setMedia(new MediaDto());
        message.setOptions(List.of("Option 1", "Option 2", "Option 3"));
        message.setBotOptions(true);
        message.setPlatform("mspace");
        message.setCreatedAt(ZonedDateTime.now());
        message.setUpdatedAt(ZonedDateTime.now());
  /*List<Message> message = List.of(
                new Message() {{
                    setId(5L);
                    setUserId("userjais123");
                    setEmail("user123@gmail.com");
                    setSendType(MessageSendType.MESSAGE);
                    setMessageType(MessageType.TEXT);
                    setMessageId("msg-001");
                    setMessageTo(MessageTo.USER);
                    setText("Hello,how are you.");
                    setReplyToMessageId("msg-0001");
                    setStatus(MessageStatus.READ);
                    setEmoji("\uD83D\uDE0A");
                    setAction(Action.SELECTED);
                    setMedia(new MediaDto());
                    setOptions(List.of("Option 1", "Option 2", "Option 3"));
                    setBotOptions(true);
                    setPlatform("mspace");
                    setCreatedAt(ZonedDateTime.now());
                    setUpdatedAt(ZonedDateTime.now());
                }}
        );
*/
       storeNewMessageInRedis(email,message);
        Pageable pageable = PageRequest.of(page, size);
        List<MessageDto> finalMessages = new ArrayList<>();

        // Fetch messages from Redis
        try {
            List<MessageDto> recentMessagesFromRedis = redisChatHistoryRepository.getChatHistoryDetailsEmail(email);
            Page<MessageDto> recentMessagesPage = convertListToPage(recentMessagesFromRedis, pageable);
            if (recentMessagesPage != null && !recentMessagesPage.isEmpty()) {
                finalMessages.addAll(recentMessagesPage.getContent());
                logger.info("Found {} recent messages from Redis for email: {}", finalMessages.size(), email);

                ZonedDateTime latestRedisDate = recentMessagesFromRedis.get(recentMessagesFromRedis.size() - 1).getCreatedAt();
                ZonedDateTime oneDayBeforeRedis = latestRedisDate.minusDays(1);
                logger.info(" date before redis  date: {}", oneDayBeforeRedis);

                try {
                    Page<MessageDto> messagesFromDbDays = chatHistoryRepository.findMessagesFromDayswrtEmail(oneDayBeforeRedis, email, pageable);
                    if (messagesFromDbDays != null && !messagesFromDbDays.isEmpty()) {
                        finalMessages.addAll(messagesFromDbDays.getContent());
                        logger.info("Found {} messages from the database for the past  days for email: {}", messagesFromDbDays.getContent().size(), email);
                    }
                } catch (Exception e) {
                    logger.error("Error fetching messages from database for the past  days for email {}: {}", email, e.getMessage());
                }
            } else {
                logger.info("No recent messages found in Redis, fetching from database for the last 90 days for email: {}", email);

                ZonedDateTime dateTime90DaysAgo = LocalDateTime.now()
                        .minusDays(90)
                        .atZone(ZoneId.systemDefault());
                try {
                    Page<MessageDto> messagesFromDb90Days = chatHistoryRepository.findMessagesFromDayswrtEmail(dateTime90DaysAgo, email, pageable);
                    if (messagesFromDb90Days != null && !messagesFromDb90Days.isEmpty()) {
                        finalMessages.addAll(messagesFromDb90Days.getContent());
                        logger.info("Found {} messages from the database for the past 90 days for email: {}", messagesFromDb90Days.getContent().size(), email);
                    } else {
                        logger.error("No messages found in the database for email: {} in the last 90 days", email);
                        throw new ResourcesNotFoundException("No messages found for the provided email in the last 90 days.");
                    }
                } catch (Exception e) {
                    logger.error("Error fetching messages from database for the last 90 days for email {}: {}", email, e.getMessage());
                    throw new ResourcesNotFoundException("Error fetching messages from database for the provided email in the last 90 days.");
                }
            }
        } catch (Exception e) {
            logger.error("Error retrieving messages for email {}: {}", email, e.getMessage());
            throw new ResourcesNotFoundException("An error occurred while retrieving messages for the provided email.");
        }

        // Handle empty result
        if (finalMessages.isEmpty()) {
            logger.error("No messages found for email: {} in the last 90 days", email);
            throw new ResourcesNotFoundException("No messages found for the provided email in the last 90 days.");
        }

        return new PageImpl<>(finalMessages, pageable, finalMessages.size());
    }

    public void storeNewMessageInRedis(String email, Message message) {
        // Store the new message both in Redis and DB
        try {
            logger.info("Storing new message in Redis and DB for email: {}", email);
            redisChatHistoryRepository.saveChatHistoryDetails(email, message);

            // Asynchronously move the message to DB if needed
           // moveMessageToDbAsync(message);
        } catch (Exception e) {
            logger.error("Error storing message in Redis for email {}: {}", email, e.getMessage());
            throw new RuntimeException("Error storing new message in Redis.");
        }
    }

  /*  @Async
    @Transactional
    public void moveMessageToDbAsync(Message message) {
        try {
            logger.info("Asynchronously saving message to DB: {}", message);
            chatHistoryRepository.save(message);
        } catch (Exception e) {
            logger.error("Error saving message asynchronously to DB: {}", e.getMessage());
        }
    }*/

    // Convert List to Page
    public Page<MessageDto> convertListToPage(List<MessageDto> list, Pageable pageable) {
        try {
            int start = Math.min((int) pageable.getOffset(), list.size());
            int end = Math.min((start + pageable.getPageSize()), list.size());
            if (start >= end) {
                return new PageImpl<>(new ArrayList<>(), pageable, list.size());
            }
            List<MessageDto> sublist = list.subList(start, end);
            return new PageImpl<>(sublist, pageable, list.size());
        } catch (Exception e) {
            logger.error("Error converting list to page: {}", e.getMessage());
            throw new RuntimeException("Error converting list to page.");
        }
    }

    /**
     * Fetches chat history for the last 3 days, paginated.
     * Tries Redis first; if missing, falls back to DB and caches in Redis.
     */
    //aded by Arti
   public   Page<MessageDto> getChatHistory(String email, int page, int size) {
        // 1) Try Redis first
       /* List<MessageDto> recentFromRedis = redisChatHistoryRepository.getChatHistoryDetailsEmail(email);
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
        }*/

        // 2) Fallback: Redis empty or out of range → load from DB
        Page<MessageDto> dbPage = getMessagesFromDB(email, page, size);
        System.out.println("db output:"+dbPage);
        if (dbPage.isEmpty()) {
            throw new ResourcesNotFoundException(
                    "No messages found for the provided email in the last 3 days.");
        }

        // 3) Optionally cache this page in Redis for next time
        //saveDataForLast3DaysToRedis(dbPage.getContent(), email);

        return dbPage;
    }

    public Page<MessageDto> getMessagesFromDB(String email, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Message> messagePage = chatHistoryRepository.findByEmail(email, pageable);
        System.out.println("db print:"+messagePage);
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