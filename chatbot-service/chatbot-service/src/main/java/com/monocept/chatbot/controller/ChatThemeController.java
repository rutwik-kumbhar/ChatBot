package com.monocept.chatbot.controller;

import com.monocept.chatbot.Entity.ElyColor;
import com.monocept.chatbot.enums.Status;
import com.monocept.chatbot.exceptions.ResourcesNotFoundException;
import com.monocept.chatbot.model.request.ElyThemeNameWithPlatform;
import com.monocept.chatbot.model.request.ElyThemeRequest;
import com.monocept.chatbot.model.response.MasterResponse;
import com.monocept.chatbot.service.impl.ElyChatThemeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/color-config")
public class ChatThemeController {

    private final ElyChatThemeService service;

    public ChatThemeController(ElyChatThemeService service) {
        this.service = service;
    }

    @PostMapping("/elychatcolor")
    public ResponseEntity<MasterResponse<ElyColor>> saveTheme(@RequestBody ElyThemeRequest request) {
        try {
            ElyColor savedTheme = service.saveTheme(
                    request.getThemeName(),
                    request.getBackgroundColor(),
                    request.getUserMessageColor(),
                    request.getBotMessageColor(),
                    request.getBorderColor(),
                    request.getButtonColor(),
                    request.getCoachOptionColor(),
                    request.getBotOptionColor(),
                    request.getPlatform()
            );

            MasterResponse<ElyColor> response = new MasterResponse<>(
                    Status.SUCCESS.name(),
                    HttpStatus.CREATED.value(),
                    "Theme saved successfully",
                    savedTheme
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception exception) {
            MasterResponse<ElyColor> response = new MasterResponse<>(
                    Status.FAILURE.name(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Failed to save theme",
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    @GetMapping("/active-theme")
    public ResponseEntity<MasterResponse<ElyColor>> getActiveTheme(@RequestBody ElyThemeNameWithPlatform request) {
        try {
            ElyColor theme = service.getActiveTheme(request.getPlatform());
            MasterResponse<ElyColor> response = new MasterResponse<>(
                    Status.SUCCESS.name(),
                    HttpStatus.OK.value(),
                    "Active theme fetched successfully",
                    theme
            );
            return ResponseEntity.ok(response);
        } catch (ResourcesNotFoundException exception) {
            MasterResponse<ElyColor> response = new MasterResponse<>(
                    Status.FAILURE.name(),
                    HttpStatus.NOT_FOUND.value(),
                    exception.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception exception) {
            MasterResponse<ElyColor> response = new MasterResponse<>(
                    Status.FAILURE.name(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error fetching theme",
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
