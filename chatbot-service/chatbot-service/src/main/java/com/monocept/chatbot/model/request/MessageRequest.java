package com.monocept.chatbot.model.request;

import lombok.*;

import javax.validation.constraints.Email;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequest {
   //@Email(message = "Invalid email format")
   // public String email;
    public String agentId;
    public int page;
    public int size;


}
