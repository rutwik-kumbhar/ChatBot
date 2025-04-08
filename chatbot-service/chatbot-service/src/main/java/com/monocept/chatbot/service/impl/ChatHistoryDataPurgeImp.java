package com.monocept.chatbot.service.impl;

import com.monocept.chatbot.reposiotry.ChatHistoryRepository;
import com.monocept.chatbot.service.ChatHistoryDataPurge;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class ChatHistoryDataPurgeImp  implements ChatHistoryDataPurge {


    private final ChatHistoryRepository chatHistoryRepository;
    private static final Logger logger = LoggerFactory.getLogger(ChatHistoryDataPurgeImp.class);

    public ChatHistoryDataPurgeImp(ChatHistoryRepository chatHistoryRepository) {
        this.chatHistoryRepository = chatHistoryRepository;
    }


    @Override
    @Transactional
    //Scheduled to run every day at midnight (adjust as necessary)
    @Scheduled(cron = "0 54 17 * * ?")
    public void deleteHistoryData90Days(){
            //LocalDateTime date90DaysAgo = LocalDateTime.now().minusDays(90);
        ZonedDateTime date90DaysAgo = LocalDateTime.now()
                .minusDays(90)
                .atZone(ZoneId.systemDefault());
        try {
            logger.info("Data purge scheduled. Deleting records older than 90 days. Date threshold: {}", date90DaysAgo);

            // Delete data older than 90 days
            chatHistoryRepository.deleteByDateTimeBefore(date90DaysAgo);
            logger.info("Old data deleted: records older than 90 days.");
        } catch (Exception e) {
            logger.error("Error occurred while purging chat history data: {}", e.getMessage(), e);
        }
        }

    }


