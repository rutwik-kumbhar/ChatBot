package com.monocept.chatbot.model.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserInfo {
    private String ssoId;
    private String agentId;
    private String name;
    private String email;
    private String role;
    private String firebaseId;
    private String deviceId;
    private String sessionId;
}
