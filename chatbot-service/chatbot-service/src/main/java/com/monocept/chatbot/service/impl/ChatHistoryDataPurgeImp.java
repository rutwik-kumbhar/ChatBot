package com.monocept.chatbot.service.impl;

import com.monocept.chatbot.reposiotry.ChatHistoryRepository;
import com.monocept.chatbot.service.ChatHistoryDataPurge;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Service
public class ChatHistoryDataPurgeImp  implements ChatHistoryDataPurge {


    private final ChatHistoryRepository chatHistoryRepository;

    public ChatHistoryDataPurgeImp(ChatHistoryRepository chatHistoryRepository) {
        this.chatHistoryRepository = chatHistoryRepository;
    }


    @Override
    @Transactional
    //Scheduled to run every day at midnight (adjust as necessary)
   //  @Scheduled(cron = "0 0 0 * * ?")
    @Scheduled(cron = "0 45 17 * * ?")

    public void deleteHistoryData90Days(){
            LocalDateTime date90DaysAgo = LocalDateTime.now().minusDays(90);
            System.out.println("day"+date90DaysAgo);
            // Delete data older than 90 days
            chatHistoryRepository.deleteByDateTimeBefore(date90DaysAgo);
            System.out.println("Old data deleted: records older than 90 days.");
        }


    }


