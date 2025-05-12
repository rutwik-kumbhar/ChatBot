package com.monocept.chatbot.model.response;

import com.monocept.chatbot.enums.MessageStatus;
import lombok.Data;

@Data
public class MLIMessageResponse {

    MessageStatus acknowledgement;
    String messageId;
}
