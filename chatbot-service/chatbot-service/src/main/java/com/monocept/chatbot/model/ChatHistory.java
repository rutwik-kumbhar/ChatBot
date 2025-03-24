package com.monocept.chatbot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_history")
public class ChatHistory {

    @Id
    @Column(name = "msg_id", length = 50)
    private String msgId;

    @Column(name = "email", length = 50, nullable = false)
    private String email;

    @Column(name = "message", columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column(name = "message_to", length = 10, nullable = false)
    private String messageTo; // "user" or "bot"

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    @Column(name = "reply_id", length = 50)
    private String replyId;

    @Column(name = "media", columnDefinition = "jsonb")
    private String media; // JSON field

    @Column(name = "activity", length = 20)
    private String activity;

    @Column(name = "session", nullable = false)
    private String session;

    @Column(name = "platform", length = 50)
    private String platform;

    // Getters and Setters
}
