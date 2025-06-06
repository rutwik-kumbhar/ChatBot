package com.monocept.chatbot.model.dto;

import lombok.*;


import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HistoryDTO implements Serializable {
    private Long  msgId;
    private String msg;
    private String messageTo;
    private LocalDateTime dateTime;
    private String replyId;
    private String type;
    private String mediaUrl;
    private String activity;


}


