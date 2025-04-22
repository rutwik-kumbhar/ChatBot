package com.monocept.chatbot.utils;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monocept.chatbot.model.request.UserInfo;
import com.monocept.chatbot.service.CognitoService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.io.InputStream;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserInfoLoader {


    private final ObjectMapper objectMapper;
    private final CognitoService cognitoService;

//    @PostConstruct
    public void init() {
        try {
            InputStream inputStream = new ClassPathResource("users.json").getInputStream();
            List<UserInfo> userList =objectMapper.readValue(inputStream, new TypeReference<List<UserInfo>>() {});
            userList.forEach(userInfo -> {
                log.info("userInfo: {}", userInfo);
                cognitoService.getCognitoToken(userInfo);
            });
            log.info("Loaded {} users from users.json", userList.size());
        } catch (Exception e) {
            log.error("Failed to load users from JSON", e);
            throw new RuntimeException(e);
        }
    }
}
