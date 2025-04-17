package com.monocept.chatbot.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monocept.chatbot.entity.User;
import com.monocept.chatbot.enums.BotCommunicationFlow;
import com.monocept.chatbot.model.request.UserInfo;
import com.monocept.chatbot.reposiotry.UserRepository;
import com.monocept.chatbot.service.CognitoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@Service
public class CognitoServiceImpl implements CognitoService {

    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;


    @Override
    public String getCognitoToken(UserInfo request) {
        log.info("getCognitoToken : Processing getCognitoToken for agentId: {}", request.getAgentId());

        // Trigger async process for Redis and DB write
        handleRedisAndDbAsync(request);

        // Optional: convert request to map for future use (not really used here)
        Map<String, String> userRequestMap = objectMapper.convertValue(request, new TypeReference<>() {});
        log.debug("getCognitoToken : Converted user request to map: {}", userRequestMap);

        //  Dummy token for now
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.VkqWNmEcj6M7X3h2ehnaQ5I9Hg2Xfi5i8dtTjvGVsNU";
        return token;
    }

    // Async method to handle Redis and DB tasks concurrently
    @Async("taskExecutor")
    public void handleRedisAndDbAsync(UserInfo request) {
        String redisKey = String.format("user:%s", request.getAgentId());
        log.info("handleRedisAndDbAsync : Processing async Redis/DB task for agentId: {}", request.getAgentId());

        try {
            // Store in Redis if the key doesn't already exist
            if (Boolean.FALSE.equals(redisTemplate.hasKey(redisKey))) {
                String requestJson = objectMapper.writeValueAsString(request);
                redisTemplate.opsForValue().set(redisKey, requestJson);
                log.info("handleRedisAndDbAsync :  Stored request in Redis for agentId: {} with key: {}", request.getAgentId(), redisKey);
            }

            // Save the user in DB if not already present
            userRepository.findByAgentId(request.getAgentId())
                    .ifPresentOrElse(
                            user -> {
                                // Log if user already exists
                                log.info("handleRedisAndDbAsync : User already exists in DB for agentId: {}", request.getAgentId());
                            },
                            () -> {
                                // Save new user if not found
                                User user = buildUserFromRequest(request);
                                userRepository.save(user);
                                log.info("handleRedisAndDbAsync : User saved in DB for agentId: {}", request.getAgentId());
                            });

        } catch (Exception e) {
            // Log error if something goes wrong during the async task
            log.error("handleRedisAndDbAsync : Error processing async Redis/DB task for agentId: {}: {}", request.getAgentId(), e.getMessage(), e);
        }
    }

    // Method to build User object from request data
    private User buildUserFromRequest(UserInfo request) {
        log.debug("buildUserFromRequest : Building user object from request for agentId: {}", request.getAgentId());

        // Construct and return User entity
        User user = User.builder()
                .agentId(request.getAgentId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .role(request.getRole())
                .firebaseId(request.getFirebaseId())
                .deviceId(request.getDeviceId())
                .statusFlag(BotCommunicationFlow.COACH) // Initial status set to 'COACH'
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now()).build();
        log.info("buildUserFromRequest : User object created for agentId: {}", request.getAgentId());
        return user;
    }
}
