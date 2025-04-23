package com.monocept.chatbot.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monocept.chatbot.entity.Option;
import com.monocept.chatbot.entity.PlaceHolder;
import com.monocept.chatbot.model.request.UserInfo;
import com.monocept.chatbot.reposiotry.OptionRepository;
import com.monocept.chatbot.reposiotry.PlaceholderRepository;
import com.monocept.chatbot.service.CognitoService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class DataLoader {

    private final ObjectMapper objectMapper;
    private final CognitoService cognitoService;
    private final OptionRepository optionRepository;
    private final PlaceholderRepository placeholderRepository;

    @PostConstruct
    public void init() {
        try {
            // Correctly load the respective JSON files
            InputStream userInputStream = new ClassPathResource("users.json").getInputStream();
            InputStream optionInputStream = new ClassPathResource("option.json").getInputStream();
            InputStream placeholderInputStream = new ClassPathResource("placeholder.json").getInputStream();

            // Correctly map the JSON files to their respective lists
            List<UserInfo> userList = objectMapper.readValue(userInputStream, new TypeReference<List<UserInfo>>() {});
            List<Option> optionList = objectMapper.readValue(optionInputStream, new TypeReference<List<Option>>() {});
            List<PlaceHolder> placeholderList = objectMapper.readValue(placeholderInputStream, new TypeReference<List<PlaceHolder>>() {});


            userList.forEach(userInfo -> cognitoService.getCognitoToken(userInfo));

            // Save the data to the repositories
            optionRepository.saveAll(optionList);
            placeholderRepository.saveAll(placeholderList);

        } catch (Exception e) {
            log.error("Failed to load data from JSON", e);
            throw new RuntimeException(e);
        }
    }
}
