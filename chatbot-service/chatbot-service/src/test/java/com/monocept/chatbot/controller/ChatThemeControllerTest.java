package com.monocept.chatbot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monocept.chatbot.Entity.ElyColor;
import com.monocept.chatbot.enums.Status;
import com.monocept.chatbot.exceptions.ResourcesNotFoundException;
import com.monocept.chatbot.model.request.ElyThemeRequest;
import com.monocept.chatbot.model.request.ElyThemeNameWithPlatform;
import com.monocept.chatbot.service.impl.ElyChatThemeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatThemeController.class)
public class ChatThemeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ElyChatThemeService service;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testSaveTheme_ShouldReturnCreated() throws Exception {
        ElyThemeRequest request = new ElyThemeRequest("dark", "#000", "#fff", "#eee", "#ddd", "#ccc", "#bbb", "#aaa", "web");
        ElyColor color = new ElyColor();
        color.setThemeName("dark");
        color.setPlatform("mspace");

        when(service.saveTheme(any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(color);

        int status = mockMvc.perform(post("/api/color-config/elychatcolor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.themeName").value("dark"))
                .andReturn().getResponse().getStatus();
        assertEquals(201, status);

    }

    @Test
    void testGetActiveTheme_ShouldReturnOk() throws Exception {
        ElyThemeNameWithPlatform request = new ElyThemeNameWithPlatform("web");
        ElyColor color = new ElyColor();
        color.setPlatform("web");
        color.setActive(true);

        when(service.getActiveTheme("web")).thenReturn(color);

        int status = mockMvc.perform(get("/api/color-config/active-theme")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.platform").value("web"))
                .andReturn().getResponse().getStatus();
        assertEquals(200, status);

    }

    @Test
    void testGetActiveTheme_ShouldReturnNotFound_OnCustomException() throws Exception {
        ElyThemeNameWithPlatform request = new ElyThemeNameWithPlatform("mspace");

        when(service.getActiveTheme("mspace"))
                .thenThrow(new ResourcesNotFoundException("Theme not found"));

        int status = mockMvc.perform(get("/api/color-config/active-theme")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(Status.FAILURE.name()))
                .andExpect(jsonPath("$.message").value("Theme not found"))
                .andReturn().getResponse().getStatus();
        assertEquals(404, status);

    }

    @Test
    void testGetActiveTheme_ShouldReturnInternalServerError_OnGenericException() throws Exception {
        ElyThemeNameWithPlatform request = new ElyThemeNameWithPlatform("mspace");

        when(service.getActiveTheme("mspace")).thenThrow(new RuntimeException("Server crashed"));

        int status = mockMvc.perform(get("/api/color-config/active-theme")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(Status.FAILURE.name()))
                .andExpect(jsonPath("$.message").value("Error fetching theme"))
                .andReturn().getResponse().getStatus();
        assertEquals(500, status);
    }

}
