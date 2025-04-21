package com.monocept.chatbot.model.dto;

import com.monocept.chatbot.enums.BotCommunicationFlow;
import com.monocept.chatbot.enums.MessageTo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceiveMessageDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private String emailId;
    private String userId;
    private BotCommunicationFlow status;
    private String platform;
    private boolean isConversationEnded;
    private Entry entry;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Entry {
        private MessageTo messageTo;
        private String replyToMessageId;
        private Message message;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Message{
        private String text;
        private boolean botOption;
        private List<String> options;
        private MediaDto media;
        private Acknowledgement acknowledgement;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Acknowledgement {
        private String status;
    }
}
