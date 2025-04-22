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
                              String coachOptionColor, String botOptionColor, String platform) {

        List<ElyColor> allThemes = chatThemeRepository.findAllByPlatformIgnoreCase(platform);
        for (ElyColor theme : allThemes) {
            theme.setActive(false);
        }
        chatThemeRepository.saveAll(allThemes);

        ZonedDateTime now = ZonedDateTime.now();

        return chatThemeRepository.findByThemeNameIgnoreCaseAndPlatformIgnoreCase(themeName, platform)
                .map(existing -> {
                    existing.setBackgroundColor(backgroundColor);
                    existing.setUserMessageColor(userMessageColor);
                    existing.setBotMessageColor(botMessageColor);
                    existing.setBorderColor(borderColor);
                    existing.setButtonColor(buttonColor);
                    existing.setCoachOptionColor(coachOptionColor);
                    existing.setBotOptionColor(botOptionColor);
                    existing.setUpdatedAt(now);
                    existing.setActive(true);
                    return chatThemeRepository.save(existing);
                }).orElseGet(() -> {
                    ElyColor theme = new ElyColor();
                    theme.setThemeName(themeName);
                    theme.setPlatform(platform); // Set platform
                    theme.setBackgroundColor(backgroundColor);
                    theme.setUserMessageColor(userMessageColor);
                    theme.setBotMessageColor(botMessageColor);
                    theme.setBorderColor(borderColor);
                    theme.setButtonColor(buttonColor);
                    theme.setCoachOptionColor(coachOptionColor);
                    theme.setBotOptionColor(botOptionColor);
                    theme.setCreatedAt(now);
                    theme.setUpdatedAt(now);
                    theme.setActive(true);
                    return chatThemeRepository.save(theme);
                });
    }

    @Override
    public ElyColor getActiveTheme(String platform) {
        return chatThemeRepository.findByIsActiveTrueAndPlatformIgnoreCase(platform)
                .orElseThrow(() -> new RuntimeException("No active theme found for platform: " + platform));
    }

}