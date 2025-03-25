package com.monocept.chatbot.Request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequest {
    public String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
