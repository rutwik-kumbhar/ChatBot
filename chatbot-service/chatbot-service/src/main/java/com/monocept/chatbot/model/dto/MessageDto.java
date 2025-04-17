package com.monocept.chatbot.model.dto;

import com.monocept.chatbot.enums.*;
import com.monocept.chatbot.utils.MediaDtoConverter;
//import jakarta.persistence.Column;
//import jakarta.persistence.Convert;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private String emailId;
    private String userId;
    private String platform;
    private MessageSendType sendType;
    private String messageId;
    private MessageTo messageTo;
    private MessageType messageType;
    private String text;
    private String replyToMessageId;
    private MessageStatus status;
    private String emoji;
    private Action action;
//    private MediaDto media;
    private boolean botOption;
    private List<String> options;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
