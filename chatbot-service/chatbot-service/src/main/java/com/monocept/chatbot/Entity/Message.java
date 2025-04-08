package com.monocept.chatbot.Entity;


import com.monocept.chatbot.enums.*;
import com.monocept.chatbot.model.dto.MediaDto;
import com.monocept.chatbot.utils.MediaDtoConverter;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "message")
public class Message {
    @Id
    @SequenceGenerator(name =  "message_seq", sequenceName = "message_seq", initialValue = 1 , allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE , generator = "message_seq")
    private Long id;

    @Column(length = 20)
    private String userId;

    @Column(length = 50)
    private String email;

    @Enumerated(EnumType.STRING)
    private MessageSendType sendType;


    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    @Column(length = 50)
    private String messageId;

    @Enumerated(EnumType.STRING)
    private MessageTo messageTo;


    @Column(columnDefinition = "TEXT")
    private String text;

    @Column(length = 50)
    private String replyToMessageId;

    @Enumerated(EnumType.STRING)
    private MessageStatus status;

    @Column(length = 30)
    private String emoji;

    @Enumerated(EnumType.STRING)
    private Action action;

    @Column(columnDefinition = "jsonb")
    @Convert(converter = MediaDtoConverter.class)
    private MediaDto media;


    @Column(columnDefinition = "jsonb")
    private List<String> botOptions;

    private boolean option;

    @Column(length =  10)
    private String platform;

    private ZonedDateTime createdAt;

    private ZonedDateTime updatedAt;

}
