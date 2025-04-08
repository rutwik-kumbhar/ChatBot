package com.monocept.chatbot.service.impl;

import com.monocept.chatbot.enums.StatusFlag;
import com.monocept.chatbot.model.request.GetUserConfigRequest;
import com.monocept.chatbot.model.response.UserConfigResponse;
import com.monocept.chatbot.reposiotry.OptionRepository;
import com.monocept.chatbot.reposiotry.PlaceholderRepository;
import com.monocept.chatbot.service.ConfigService;
import com.monocept.chatbot.service.OptionService;
import com.monocept.chatbot.service.PlaceholderService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ConfigServiceImpl implements ConfigService {


    private  final OptionService optionService;
    private final PlaceholderService placeholderService;
    private final RedisTemplate<String, Object> redisTemplate;

    public ConfigServiceImpl(OptionService optionService, PlaceholderService placeholderService, RedisTemplate<String, Object> redisTemplate) {
        this.optionService = optionService;
        this.placeholderService = placeholderService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public UserConfigResponse getConfiguration(GetUserConfigRequest request) {


        List<String> options = optionService.getAllOptions();
        List<String> placeholders
                = placeholderService.getAllPlaceholders();

        Optional<Map<String, Object>> userInfoData = getUserInfoData(request.getAgentId());
        return UserConfigResponse.builder()
                .userInfo(userInfoData.orElse(null))
                .options(options)
                .placeHolders(placeholders)
                .botName("Ely") // Need to fetch later from db
                .statusFlag(StatusFlag.COACH)
                .dateTime(ZonedDateTime.now().toString()).build();
    }

    public Optional<Map<String, Object>> getUserInfoData(String agentId) {
        String redisKey = "user:request:" + agentId;
        return Optional.ofNullable(redisTemplate.opsForValue().get(redisKey))
                .filter(Map.class::isInstance)
                .map(obj -> (Map<String, Object>) obj);
    }
}
