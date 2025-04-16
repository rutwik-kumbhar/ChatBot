package com.monocept.chatbot.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monocept.chatbot.entity.User;
import com.monocept.chatbot.enums.BotCommunicationFlow;
import com.monocept.chatbot.model.request.UserInfo;
import com.monocept.chatbot.reposiotry.UserRepository;
import com.monocept.chatbot.service.CognitoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Map;

@Slf4j
@Service
public class CognitoServiceImpl implements CognitoService {

    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public CognitoServiceImpl(UserRepository userRepository, RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public String getCognitoToken(UserInfo request) {
        // Trigger async process for Redis and DB write
        handleRedisAndDbAsync(request);

        // Optional: convert request to map for future use (not really used here)
        Map<String, String> userRequestMap = objectMapper.convertValue(request, new TypeReference<>() {});
        return "dummy-token-cognito";
    }

    @Async("taskExecutor")
    public void handleRedisAndDbAsync(UserInfo request) {
        String redisKey = String.format("user:request:%s", request.getAgentId());

        try {
            // Store in Redis if key doesn't exist
            if (Boolean.FALSE.equals(redisTemplate.hasKey(redisKey))) {
                String requestJson = objectMapper.writeValueAsString(request);
                redisTemplate.opsForValue().set(redisKey, requestJson);
                log.info("Stored request in Redis for agentId: {}", request.getAgentId());
            }

            // Save in DB if user not already present
            userRepository.findByAgentId(request.getAgentId())
                    .ifPresentOrElse(
                            user -> log.info("User already exists in DB for agentId: {}", request.getAgentId()),
                            () -> {
                                User user = buildUserFromRequest(request);
                                userRepository.save(user);
                                log.info("User saved in DB for agentId: {}", request.getAgentId());
                            });

        } catch (Exception e) {
            log.error("Error processing async Redis/DB task for agentId: {} {}", request.getAgentId(), e.getMessage());
        }
    }

    private User buildUserFromRequest(UserInfo request) {
        return User.builder()
                .agentId(request.getAgentId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .role(request.getRole())
                .firebaseId(request.getFirebaseId())
                .deviceId(request.getDeviceId())
                .statusFlag(BotCommunicationFlow.COACH)
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();
    }
}
