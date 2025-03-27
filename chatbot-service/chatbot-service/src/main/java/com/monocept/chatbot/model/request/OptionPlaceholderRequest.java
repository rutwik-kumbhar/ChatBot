package com.monocept.chatbot.model.request;


import com.monocept.chatbot.enums.MethodType;
import lombok.Data;

import java.util.List;


@Data
public class OptionPlaceholderRequest {
    private List<String> name;
    private MethodType methodType;
}
