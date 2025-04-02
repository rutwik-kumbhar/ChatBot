package com.monocept.chatbot.Entity;

import com.monocept.chatbot.model.dto.MediaDto;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Type;

import java.time.ZonedDateTime;

@Entity
@Data
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
//    @Type(JsonType.class) // Use Hibernate JSON Type
    @Column(columnDefinition = "jsonb")
//    private MediaDto media;
    private Boolean botOption;
    private String options;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
