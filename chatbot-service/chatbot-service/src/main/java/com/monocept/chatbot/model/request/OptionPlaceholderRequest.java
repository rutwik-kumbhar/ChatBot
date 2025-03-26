package com.monocept.chatbot.model.request;

import com.monocept.chatbot.enums.MethodType;
import lombok.Data;


@Data
public class OptionPlaceholderRequest {
    private String filled;
    private String name;
    private MethodType methodType;
}
