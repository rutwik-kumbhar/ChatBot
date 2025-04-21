package com.monocept.chatbot.service.impl;

import com.monocept.chatbot.Entity.ElyColor;

import java.time.ZonedDateTime;
import java.util.List;

public interface ElyChatThemeService {
  //  ElyColor getThemeByName(String themeName);

    public ElyColor getActiveTheme();

  //  List<ElyColor> getAllThemes();
    ElyColor saveTheme(String themeName, String backgroundColor, String userMessageColor,
                       String botMessageColor, String borderColor, String buttonColor,
                       String coachOptionColor, String botOptionColor);
}
