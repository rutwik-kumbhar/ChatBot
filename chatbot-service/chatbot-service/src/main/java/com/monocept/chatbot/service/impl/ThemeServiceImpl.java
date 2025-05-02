package com.monocept.chatbot.service.impl;

import com.monocept.chatbot.entity.Theme;
import com.monocept.chatbot.model.request.ThemeRequest;
import com.monocept.chatbot.reposiotry.ChatThemeRepository;
import com.monocept.chatbot.service.ThemeService;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Objects;

@Service
public class ThemeServiceImpl implements ThemeService {

    private final ChatThemeRepository chatThemeRepository;

    public ThemeServiceImpl(ChatThemeRepository chatThemeRepository) {
        this.chatThemeRepository = chatThemeRepository;
    }

    @Override
    public Theme saveOrUpdateTheme(ThemeRequest request) {

        Theme theme = chatThemeRepository
                .findByThemeNameIgnoreCaseAndPlatformIgnoreCase(request.getThemeName(), request.getPlatform())
                .orElseGet(Theme::new);

        boolean isNew = theme.getId() == null;

        setIfNotNull(request.getThemeName(), theme::setThemeName);
        setIfNotNull(request.getPlatform(), theme::setPlatform);
        setIfNotNull(request.getBackgroundColor(), theme::setBackgroundColor);
        setIfNotNull(request.getUserMessageColor(), theme::setUserMessageColor);
        setIfNotNull(request.getBotMessageColor(), theme::setBotMessageColor);
        setIfNotNull(request.getBorderColor(), theme::setBorderColor);
        setIfNotNull(request.getButtonColor(), theme::setButtonColor);
        setIfNotNull(request.getCoachOptionColor(), theme::setCoachOptionColor);
        setIfNotNull(request.getBotOptionColor(), theme::setBotOptionColor);

        theme.setUpdatedAt(ZonedDateTime.now());
        theme.setActive(true);

        if (isNew) {
            chatThemeRepository.updateThemeStatus(false, request.getPlatform());
            theme.setCreatedAt(ZonedDateTime.now());
        }else {
            theme.setUpdatedAt(ZonedDateTime.now());
        }

        return chatThemeRepository.save(theme);
    }

    @Override
    public Theme getActiveTheme(String platform) {
        return chatThemeRepository.findByIsActiveTrueAndPlatformIgnoreCase(platform)
                .orElseThrow(() -> new RuntimeException("No active theme found for platform: " + platform));
    }

    private void setIfNotNull(String value, java.util.function.Consumer<String> setter) {
        if (Objects.nonNull(value)) {
            setter.accept(value);
        }
    }
}
