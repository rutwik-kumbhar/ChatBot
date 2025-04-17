package com.monocept.chatbot.service.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monocept.chatbot.enums.BotCommunicationFlow;
import com.monocept.chatbot.model.dto.NameIconDto;
import com.monocept.chatbot.model.request.GetUserConfigRequest;
import com.monocept.chatbot.model.request.UserInfo;
import com.monocept.chatbot.model.response.UserConfigResponse;
import com.monocept.chatbot.service.ConfigService;
import com.monocept.chatbot.service.OptionService;
import com.monocept.chatbot.service.PlaceholderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ConfigServiceImpl implements ConfigService {


    @Value("${chatbot.name}")
    private String chatBotName;

    private  final OptionService optionService;
    private final PlaceholderService placeholderService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;


    @Override
    public UserConfigResponse getConfiguration(GetUserConfigRequest request) {

        List<NameIconDto> options = optionService.getAllOptions();
        List<NameIconDto> placeholders = placeholderService.getAllPlaceholders();
        Optional<UserInfo> userInfo = getUserInfoDataFromRedis(request.getAgentId());
        return UserConfigResponse.builder()
                .userInfo(userInfo.orElse(null))
                .options(options)
                .placeHolders(placeholders)
                .botName(chatBotName)
                .statusFlag(BotCommunicationFlow.COACH)
                .dateTime(ZonedDateTime.now().toString()).build();
    }

    public Optional<UserInfo> getUserInfoDataFromRedis(String agentId) {
        String redisKey = String.format("user:%s", agentId);
        try {
            Object object = redisTemplate.opsForValue().get(redisKey);
            if (object != null) {
                UserInfo userInfo = objectMapper.readValue(object.toString(), UserInfo.class);
                return Optional.of(userInfo);
            } else {
                log.error("getUserInfoDataFromRedis : No user info found in Redis for agentId: {}", agentId);
            }
        } catch (Exception e) {
            log.error("getUserInfoDataFromRedis : Error while fetching user info from Redis for agentId: {}", agentId, e.getMessage());
        }
        return Optional.empty();
    }

}
