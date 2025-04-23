package com.monocept.chatbot.service;

import com.monocept.chatbot.Entity.ElyColor;
import com.monocept.chatbot.reposiotry.ChatThemeRepository;
import com.monocept.chatbot.service.impl.ElyChatThemeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ElyChatThemeServiceImplTest {
    @Mock
    private ChatThemeRepository chatThemeRepository;

    @InjectMocks
    private ElyChatThemeServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveTheme_NewTheme_ShouldCreateNew() {
        String platform = "web";
        String themeName = "dark";

        when(chatThemeRepository.findAllByPlatformIgnoreCase(platform)).thenReturn(new ArrayList<>());
        when(chatThemeRepository.findByThemeNameIgnoreCaseAndPlatformIgnoreCase(themeName, platform)).thenReturn(Optional.empty());

        ElyColor saved = new ElyColor();
        saved.setThemeName(themeName);
        saved.setPlatform(platform);
        saved.setActive(true);

        when(chatThemeRepository.save(any(ElyColor.class))).thenReturn(saved);

        ElyColor result = service.saveTheme(themeName, "#000000", "#FFFFFF", "#EEEEEE", "#DDDDDD", "#CCCCCC", "#BBBBBB", "#AAAAAA", platform);

        assertEquals(themeName, result.getThemeName());
        assertTrue(result.isActive());
        verify(chatThemeRepository, times(1)).save(any(ElyColor.class));
    }

    @Test
    void testSaveTheme_ExistingTheme_ShouldUpdate() {
        String platform = "web";
        String themeName = "light";

        ElyColor existing = new ElyColor();
        existing.setThemeName(themeName);
        existing.setPlatform(platform);
        existing.setActive(false);

        when(chatThemeRepository.findAllByPlatformIgnoreCase(platform)).thenReturn(Collections.singletonList(existing));
        when(chatThemeRepository.findByThemeNameIgnoreCaseAndPlatformIgnoreCase(themeName, platform)).thenReturn(Optional.of(existing));
        when(chatThemeRepository.save(any(ElyColor.class))).thenReturn(existing);

        ElyColor result = service.saveTheme(themeName, "#000000", "#FFFFFF", "#EEEEEE", "#DDDDDD", "#CCCCCC", "#BBBBBB", "#AAAAAA", platform);

        assertTrue(result.isActive());
        verify(chatThemeRepository).saveAll(any());
    }

    @Test
    void testGetActiveTheme_WhenExists() {
        String platform = "android";
        ElyColor color = new ElyColor();
        color.setPlatform(platform);
        color.setActive(true);

        when(chatThemeRepository.findByIsActiveTrueAndPlatformIgnoreCase(platform)).thenReturn(Optional.of(color));

        ElyColor result = service.getActiveTheme(platform);

        assertEquals(platform, result.getPlatform());
        assertTrue(result.isActive());
    }

    @Test
    void testGetActiveTheme_WhenNotExists_ShouldThrowException() {
        when(chatThemeRepository.findByIsActiveTrueAndPlatformIgnoreCase("ios")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.getActiveTheme("ios");
        });

        assertEquals("No active theme found for platform: ios", exception.getMessage());
    }

}
