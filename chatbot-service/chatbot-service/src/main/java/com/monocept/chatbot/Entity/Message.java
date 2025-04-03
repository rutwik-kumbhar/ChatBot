package com.monocept.chatbot.entity;

import com.monocept.chatbot.model.dto.MediaDto;
import com.monocept.chatbot.utils.MediaDtoConverter;
import jakarta.persistence.*;

import java.time.ZonedDateTime;

@Entity
public class Message {
    @Id
    @SequenceGenerator(name =  "message_seq", sequenceName = "message_seq", initialValue = 1 , allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE , generator = "message_seq")
    private Long id;
    private String emailId;
    private String platform;
    private String sendType;
    private String messageId;
    private String messageTo;
    private String messageType;
    private String text;
    private String replyToMessageId;
    private String status;
    private String emoji;
    private String action;
    @Column(columnDefinition = "jsonb")
    @Convert(converter = MediaDtoConverter.class)
    private MediaDto media;  
    private Boolean botOption;
    private String options;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
