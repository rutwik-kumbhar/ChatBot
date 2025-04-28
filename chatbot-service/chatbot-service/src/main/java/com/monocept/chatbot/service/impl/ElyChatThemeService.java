package com.monocept.chatbot.service.impl;

import com.monocept.chatbot.Entity.ElyColor;


public interface ElyChatThemeService {
    ElyColor getActiveTheme(String platform);
    ElyColor saveTheme(String themeName, String backgroundColor, String userMessageColor,
                       String botMessageColor, String borderColor, String buttonColor,
                       String coachOptionColor, String botOptionColor, String platform);

}
