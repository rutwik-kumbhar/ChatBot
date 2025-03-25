package com.monocept.chatbot.ChatDTO;

import lombok.*;


import java.time.LocalDateTime;

@Data
@Setter
@Getter
public class HistoryDTO {
    private Long  msgId;
    private String msg;
    private String messageTo;
    private LocalDateTime dateTime;
    private String replyId;
    private String type;

    public HistoryDTO(String type, String replyId, LocalDateTime dateTime, String messageTo, String msg, Long msgId) {
        this.type = type;
        this.replyId = replyId;
        this.dateTime = dateTime;
        this.messageTo = messageTo;
        this.msg = msg;
        this.msgId = msgId;
    }
}
