package com.monocept.chatbot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.Table;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "chat_history")
public class History {
    @Id
    //  @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long msgId;
    private String msg;
    private String messageTo;

    @Column(name = "date_time")
    private LocalDateTime dateTime;

    @Column(name = "reply_id")
    private String replyId;

    private String type;

    @Column(name = "media_url")
    private String mediaUrl;

    private String activity;

    @Column(name = "ssoid")
    private String ssoid;

    @Column(name = "agent_id")
    private String agentId;

    @Column(name = "email")
    private String email;

    @Column(name = "session")
    private String session;

}

