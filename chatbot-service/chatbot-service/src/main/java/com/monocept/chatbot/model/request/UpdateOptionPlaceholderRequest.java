package com.monocept.chatbot.model.request;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateOptionPlaceholderRequest {

    private String oldValue;
    private String newValue;
}
