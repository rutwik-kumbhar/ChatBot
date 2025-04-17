/*package com.monocept.chatbot.service.impl;

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
        log.info("getAllPlaceholders: Start fetching all placeholder names");
         return placeholderRepository.findPlaceholderNames();

    }

    @Override
    public List<PlaceHolder> addPlaceholders(OptionPlaceholderRequest optionPlaceholderRequest) {
        log.info("addPlaceholders: Start adding new placeholders");

        List<PlaceHolder> placeholders = optionPlaceholderRequest.getData()
                .stream()
                .map(data -> PlaceHolder.builder()
                        .name(data.getName())
                        .icon(data.getIcon())
                        .build())
                .toList();

        List<PlaceHolder> savedPlaceholders = placeholderRepository.saveAll(placeholders);
        log.info("addPlaceholders: Successfully added {} new placeholders", savedPlaceholders.size());
        return savedPlaceholders;
    }

    @Override
    public String updatePlaceholder(String name) {
        log.info("updatePlaceholder: Start updating placeholder with name: {}", name);
        int rowsAffected = placeholderRepository.updatePlaceholderByName(name);
        if (rowsAffected == 0) {
            log.info("updatePlaceholder: No placeholder found with name '{}' to update", name);
            return "No matching placeholder found for update.";
        } else {
            log.info("updatePlaceholder: Successfully updated placeholder with name '{}'", name);
            return "Placeholder updated successfully.";
        }
    }

    @Override
    public String deletePlaceholder(String name) {
        log.info("deletePlaceholder: Start deleting placeholder with name: {}", name);
        int rowsAffected = placeholderRepository.deleteByPlaceholderName(name);
        if (rowsAffected == 0) {
            log.info("deletePlaceholder: No placeholder found with name '{}' to delete", name);
            return "No matching placeholder found for deletion.";
        } else {
            log.info("deletePlaceholder: Successfully deleted placeholder with name '{}'", name);
            return "Placeholder deleted successfully.";
        }
    }

    @Override
    public UpdationAcknowledgmentResponse<PlaceHolder> placeholderDataHandler(OptionPlaceholderRequest request) {
        log.info("placeholderDataHandler: Start handling placeholder data update");
        log.info("placeholderDataHandler: Deleting all existing placeholders");
        placeholderRepository.deleteAllOptions();
        List<PlaceHolder> placeHolders = addPlaceholders(request);
        log.info("placeholderDataHandler: Successfully added {} new placeholder records", placeHolders.size());
        return new UpdationAcknowledgmentResponse<>(placeHolders.size(), placeHolders);
    }
}
*/