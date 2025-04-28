package com.monocept.chatbot.model.request;

import com.monocept.chatbot.enums.BotCommunicationFlow;
import com.monocept.chatbot.enums.MessageTo;
import com.monocept.chatbot.model.dto.MediaDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReceiveMessageRequest {
    private String emailId;
    private String userId;
    private BotCommunicationFlow status;
    private String platform;
    private boolean isConversationEnded;
    private Entry entry;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Entry {
        private MessageTo messageTo;
        private String replyToMessageId;
        private Message message;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Message{
        private String text;
        private String table;
        private boolean botOption;
//        private List<String> options;
        private MediaDto media;
        private Acknowledgement acknowledgement;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Acknowledgement {
        private String status;
    }
}
