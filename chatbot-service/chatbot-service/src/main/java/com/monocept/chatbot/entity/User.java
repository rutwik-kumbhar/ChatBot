package com.monocept.chatbot.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Table(name = "users")
@Entity
public class User {

    private String ssoId;
    private String agentId;
    private String name;
    private String email;
    private String role;
    private String firebaseId;
    private String deviceId;
    private String sessionId;
    private String createdAt;
    private String updatedAt;

}
