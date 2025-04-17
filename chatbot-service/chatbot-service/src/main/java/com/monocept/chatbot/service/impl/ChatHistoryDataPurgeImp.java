package com.monocept.chatbot.service.impl;

import com.monocept.chatbot.reposiotry.ChatHistoryRepository;
import com.monocept.chatbot.service.ChatHistoryDataPurge;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
@Configuration
@Transactional
public class ChatHistoryDataPurgeImp implements ChatHistoryDataPurge {

    private final ChatHistoryRepository chatHistoryRepository;
    private final TaskScheduler taskScheduler;
    private static final Logger logger = LoggerFactory.getLogger(ChatHistoryDataPurgeImp.class);
    private final int nDataDays = 90;

    @Value("${chat.history.cron}")
    private String cronExpression;

    public ChatHistoryDataPurgeImp(ChatHistoryRepository chatHistoryRepository, TaskScheduler taskScheduler) {
        this.chatHistoryRepository = chatHistoryRepository;
        this.taskScheduler = taskScheduler;
    }

    @PostConstruct
    public void schedulePurgeTask() {
        logger.info("Scheduling data purge task with cron expression: {}", cronExpression);
        taskScheduler.schedule(this::deleteDataOlderThanNDays, new CronTrigger(cronExpression));
    }

    @Override
    @Transactional
    public void deleteDataOlderThanNDays() {
        ZonedDateTime pastDateByNDays = LocalDateTime.now()
                .minusDays(nDataDays)
                .atZone(ZoneId.systemDefault());
        try {
            logger.info("Data purge running. Deleting records older than {} days.", nDataDays);
            chatHistoryRepository.deleteByDateTimeBefore(pastDateByNDays);
            logger.info("Records deleted older than {} days",nDataDays);
        } catch (Exception e) {
            logger.error("Error occurred while purging chat history data: {}", e.getMessage(), e);
        }
    }
}
