package com.monocept.chatbot.controller;

import com.monocept.chatbot.enums.Status;
import com.monocept.chatbot.model.request.UserInfo;
import com.monocept.chatbot.model.response.MasterResponse;
import com.monocept.chatbot.service.CognitoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
public class CognitoController {

    private final  CognitoService cognitoService;


    public CognitoController(CognitoService cognitoService) {
        this.cognitoService = cognitoService;
    }

    @PostMapping("/cognito-token")
    public ResponseEntity<MasterResponse<Map<String, String>>> generateToken(@RequestBody UserInfo request) {
        MasterResponse<Map<String, String>> response;
        try {
            String token = cognitoService.getCognitoToken(request);
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("token", token);
            response = new MasterResponse<>(Status.SUCCESS.name(), HttpStatus.OK.value(), "Token Created Successfully", responseBody);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error while generating token: {}", e.getMessage(), e); // optional logging
            response = new MasterResponse<>(Status.FAILURE.name(), HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to create token", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
