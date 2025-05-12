package com.monocept.chatbot.service;

import com.monocept.chatbot.entity.Theme;
import com.monocept.chatbot.model.request.ThemeRequest;


public interface ThemeService {
    Theme getActiveTheme(String platform);
    Theme saveOrUpdateTheme(ThemeRequest request);

}
