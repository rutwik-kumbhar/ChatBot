package com.monocept.chatbot.service.impl;

import com.monocept.chatbot.entity.PlaceHolder;
import com.monocept.chatbot.model.dto.NameIconDto;
import com.monocept.chatbot.model.request.OptionPlaceholderRequest;
import com.monocept.chatbot.model.request.UpdationAcknowledgmentResponse;
import com.monocept.chatbot.reposiotry.PlaceholderRepository;
import com.monocept.chatbot.service.PlaceholderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PlaceHolderServiceImpl implements PlaceholderService {

    private final PlaceholderRepository placeholderRepository;

    @Override
    public List<NameIconDto> getAllPlaceholders() {
        log.info("getAllPlaceholders : Start : Fetching all placeholder names");
         return placeholderRepository.findPlaceholderNames();

    }

    @Override
    public List<PlaceHolder> addPlaceholders(OptionPlaceholderRequest optionPlaceholderRequest) {
        log.info("Adding new placeholders");

        List<PlaceHolder> placeholders = optionPlaceholderRequest.getData()
                .stream()
                .map(data -> PlaceHolder.builder()
                        .name(data.getName())
                        .icon(data.getIcon())
                        .build())
                .toList();

        List<PlaceHolder> savedPlaceholders = placeholderRepository.saveAll(placeholders);
        log.info("Added {} placeholders", savedPlaceholders.size());
        return savedPlaceholders;
    }

    @Override
    public String updatePlaceholder(String name) {
        log.info("Updating placeholder with name: {}", name);
        int rowsAffected = placeholderRepository.deleteByPlaceholderName(name);
        if (rowsAffected == 0) {
            log.warn("No placeholder found with name: {} to update", name);
            return "No matching record found to update.";
        } else {
            log.info("Successfully updated placeholder: {}", name);
            return "Record updated.";
        }
    }

    @Override
    public String deletePlaceholder(String name) {
        log.info("Deleting placeholder with name: {}", name);
        int rowsAffected = placeholderRepository.deleteByPlaceholderName(name);
        if (rowsAffected == 0) {
            log.warn("No placeholder found with name: {} to delete", name);
            return "No matching record found to update.";
        } else {
            log.info("Successfully deleted placeholder: {}", name);
            return "Record deleted.";
        }
    }

    @Override
    public UpdationAcknowledgmentResponse<PlaceHolder> placeholderDataHandler(OptionPlaceholderRequest request) {
        log.info("Handling placeholder data update request");
        placeholderRepository.deleteAllOptions();
        log.info("Deleted all existing placeholders");

        List<PlaceHolder> placeHolders = addPlaceholders(request);

        log.info("Successfully handled placeholder data update. Total new records: {}", placeHolders.size());
        return new UpdationAcknowledgmentResponse<>(placeHolders.size(), placeHolders);
    }
}

