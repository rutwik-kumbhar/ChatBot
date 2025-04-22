package com.monocept.chatbot.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ElyThemeRequest {
    private String themeName;
    private String backgroundColor;
    private String userMessageColor;
    private String botMessageColor;
    private String borderColor;
    private String buttonColor;
    private String coachOptionColor;
    private String botOptionColor;
    private String platform;
}
