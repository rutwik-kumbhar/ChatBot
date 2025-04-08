package com.monocept.chatbot.controller;

import com.monocept.chatbot.enums.Status;
import com.monocept.chatbot.model.request.GetUserConfigRequest;
import com.monocept.chatbot.model.response.MasterResponse;
import com.monocept.chatbot.model.response.UserConfigResponse;
import com.monocept.chatbot.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
public class ConfigController {

    private final  ConfigService configService;

    public ConfigController(ConfigService configService) {
        this.configService = configService;
    }

    @PostMapping("/user/config")
    public ResponseEntity<MasterResponse<UserConfigResponse>> getUserConfig(@RequestBody GetUserConfigRequest request) {
        log.info("getUserConfig  {}", request );
        try {
            UserConfigResponse userConfiguration = configService.getConfiguration(request);
            MasterResponse<UserConfigResponse> response = new MasterResponse<>(Status.SUCCESS.name(), HttpStatus.OK.value(), "User config fetched successfully", userConfiguration);
            return ResponseEntity.ok(response);
        } catch (Exception exception) {
            MasterResponse<UserConfigResponse> response = new MasterResponse<>(Status.FAILURE.name(), HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
