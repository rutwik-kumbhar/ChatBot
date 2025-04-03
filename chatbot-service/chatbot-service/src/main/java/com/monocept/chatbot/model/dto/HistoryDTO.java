package com.monocept.chatbot.model.dto;

import com.monocept.chatbot.enums.MessageTo;
import com.monocept.chatbot.enums.MessageType;
import lombok.*;


import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HistoryDTO {
    private Long  id;
    private String messageId;
    private MessageTo messageTo;
    private String text;
    private ZonedDateTime createdAt;
    private MessageType messageType;
    private String action;

}
