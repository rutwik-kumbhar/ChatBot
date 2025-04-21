package com.monocept.chatbot.service.impl;

import com.monocept.chatbot.Entity.ElyColor;
import com.monocept.chatbot.reposiotry.ChatThemeRepository;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
@Service
public class ElyChatThemeServiceImpl implements ElyChatThemeService {

    private final ChatThemeRepository chatThemeRepository;

    public ElyChatThemeServiceImpl(ChatThemeRepository chatThemeRepository) {
        this.chatThemeRepository = chatThemeRepository;
    }

    @Override
    public ElyColor saveTheme(String themeName, String backgroundColor, String userMessageColor,
                              String botMessageColor, String borderColor, String buttonColor,
                              String coachOptionColor, String botOptionColor) {

        // Step 1: Deactivate all existing themes
        List<ElyColor> allThemes = chatThemeRepository.findAll();
        for (ElyColor theme : allThemes) {
            theme.setActive(false);
        }
        chatThemeRepository.saveAll(allThemes);

        ZonedDateTime now = ZonedDateTime.now();

        // Create or update the new theme and set it as active
        return chatThemeRepository.findByThemeNameIgnoreCase(themeName)
                .map(existing -> {
                    existing.setBackgroundColor(backgroundColor);
                    existing.setUserMessageColor(userMessageColor);
                    existing.setBotMessageColor(botMessageColor);
                    existing.setBorderColor(borderColor);
                    existing.setButtonColor(buttonColor);
                    existing.setCoachOptionColor(coachOptionColor);
                    existing.setBotOptionColor(botOptionColor);
                    existing.setUpdatedAt(now);
                    existing.setActive(true); // Mark as active
                    return chatThemeRepository.save(existing);
                }).orElseGet(() -> {
                    ElyColor theme = new ElyColor();
                    theme.setThemeName(themeName);
                    theme.setBackgroundColor(backgroundColor);
                    theme.setUserMessageColor(userMessageColor);
                    theme.setBotMessageColor(botMessageColor);
                    theme.setBorderColor(borderColor);
                    theme.setButtonColor(buttonColor);
                    theme.setCoachOptionColor(coachOptionColor);
                    theme.setBotOptionColor(botOptionColor);
                    theme.setCreatedAt(now);
                    theme.setUpdatedAt(now);
                    theme.setActive(true); // Mark as active
                    return chatThemeRepository.save(theme);
                });
    }


    @Override
    public ElyColor getActiveTheme() {
        return chatThemeRepository.findByIsActiveTrue()
                .orElseThrow(() -> new RuntimeException("No active theme found"));
    }


}