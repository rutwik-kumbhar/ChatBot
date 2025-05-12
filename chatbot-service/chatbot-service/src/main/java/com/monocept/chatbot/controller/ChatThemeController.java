package com.monocept.chatbot.controller;

import com.monocept.chatbot.entity.Theme;
import com.monocept.chatbot.enums.Status;
import com.monocept.chatbot.exceptions.ResourcesNotFoundException;
import com.monocept.chatbot.model.request.ElyThemeNameWithPlatform;
import com.monocept.chatbot.model.request.ThemeRequest;
import com.monocept.chatbot.model.response.MasterResponse;
import com.monocept.chatbot.service.ThemeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/chatbot")
public class ChatThemeController {

    private final ThemeService service;

    public ChatThemeController(ThemeService service) {
        this.service = service;
    }

    @PostMapping("/theme")
    public ResponseEntity<MasterResponse<Theme>> saveTheme(@RequestBody ThemeRequest request) {
        log.info("saveTheme : start ");
        try {
            Theme savedTheme = service.saveOrUpdateTheme(request);

            MasterResponse<Theme> response = new MasterResponse<>(Status.SUCCESS.name(),
                    HttpStatus.CREATED.value(),
                    "Theme saved successfully",
                    savedTheme
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception exception) {
            MasterResponse<Theme> response = new MasterResponse<>(
                    Status.FAILURE.name(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Failed to save theme" + exception.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    @GetMapping("/active-theme")
    public ResponseEntity<MasterResponse<Theme>> getActiveTheme(@RequestBody ElyThemeNameWithPlatform request) {
        try {
            Theme theme = service.getActiveTheme(request.getPlatform());
            MasterResponse<Theme> response = new MasterResponse<>(
                    Status.SUCCESS.name(),
                    HttpStatus.OK.value(),
                    "Active theme fetched successfully",
                    theme
            );
            return ResponseEntity.ok(response);
        } catch (ResourcesNotFoundException exception) {
            MasterResponse<Theme> response = new MasterResponse<>(
                    Status.FAILURE.name(),
                    HttpStatus.NOT_FOUND.value(),
                    exception.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception exception) {
            MasterResponse<Theme> response = new MasterResponse<>(
                    Status.FAILURE.name(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error fetching theme" + exception.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
