package com.monocept.chatbot.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.ZonedDateTime;

@Table(name = "users")
@Entity
public class User {

    @Column(length = 10)
    private String ssoId;

    @Column(length = 10)
    private String agentId;

    @Column(length =  100)
    private String name;

    @Column(length = 30)
    private String email;

    @Column(length = 20)
    private String role;

    @Column(length = 100)
    private String firebaseId;

    @Column(length = 100)
    private String deviceId;
    @Column(length = 100)
    private String sessionId;


    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

}
