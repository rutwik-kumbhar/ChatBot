package com.monocept.chatbot.model.dto;

import com.monocept.chatbot.enums.*;
import com.monocept.chatbot.utils.MediaDtoConverter;
import jakarta.persistence.Convert;
import lombok.*;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto implements Serializable  {
    private static final long serialVersionUID = 1L;
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
//    @Convert(converter = MediaDtoConverter.class)
    private MediaDto media;
    private List<String> options;
    private boolean botOptions;
    private String platform;
    private ZonedDateTime createdAt;


}
