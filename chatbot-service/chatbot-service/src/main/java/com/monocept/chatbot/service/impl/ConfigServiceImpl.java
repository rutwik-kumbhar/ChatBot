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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ConfigServiceImpl implements ConfigService {


    private  final OptionService optionService;
    private final PlaceholderService placeholderService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;


    @Override
    public UserConfigResponse getConfiguration(GetUserConfigRequest request) {

        List<NameIconDto> options = optionService.getAllOptions();
        List<NameIconDto> placeholders = placeholderService.getAllPlaceholders();
        Optional<UserInfo> userInfo = getUserInfoData(request.getAgentId());
        return UserConfigResponse.builder()
                .userInfo(userInfo.orElse(null))
                .options(options)
                .placeHolders(placeholders)
                .botName("Ely") // Need to fetch later from db
                .statusFlag(BotCommunicationFlow.COACH)
                .dateTime(ZonedDateTime.now().toString()).build();
    }

    public Optional<UserInfo> getUserInfoData(String agentId) {
        String redisKey = "user:request:" + agentId;
        try {
            Object object = redisTemplate.opsForValue().get(redisKey);
            if (object != null) {
                UserInfo userInfo = objectMapper.readValue((JsonParser) object, UserInfo.class);
                return Optional.of(userInfo);
            } else {
                log.warn("No user info found in Redis for agentId: {}", agentId);
            }
        } catch (Exception e) {
            log.error("Error while fetching user info from Redis for agentId: {}", agentId, e);
        }

        return Optional.empty();
    }

}
