package com.monocept.chatbot.model.request;

import com.monocept.chatbot.enums.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendMessageRequest {
    private String emailId;
    private String userId;
    private String platform;
    private MessageSendType sendType;
    private MessageTo messageTo;
    private MessageType messageType;
    private String text;
    private String replyToMessageId;
    private MessageStatus status;// for sendType = ack
    private String emoji; // for sendType = reaction
    private Action action;// for send Type = reaction
//    private MediaDto media; not in scope
    private boolean botOption;
    private List<String> options;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private String messageId;
}
