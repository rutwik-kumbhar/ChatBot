package com.monocept.chatbot.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReceiveMessageResponse {

    String messageId;
    long timestamp;
}
