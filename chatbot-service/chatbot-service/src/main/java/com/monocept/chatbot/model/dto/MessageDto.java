package com.monocept.chatbot.model.dto;

import com.monocept.chatbot.enums.*;
import lombok.*;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto implements Serializable  {
    private Long id;
    private String userId;
    private String email;
    private MessageSendType sendType;
    private MessageType messageType;
    private String messageId;
    private MessageTo messageTo;
    private String text;
    private String replyToMessageId;
    private MessageStatus status;
    private String emoji;
    private Action action;
    private MediaDto media;
    private List<String> botOptions;
    private boolean option;
    private String platform;
    private ZonedDateTime createdAt;
   // private ZonedDateTime updatedAt;

}
