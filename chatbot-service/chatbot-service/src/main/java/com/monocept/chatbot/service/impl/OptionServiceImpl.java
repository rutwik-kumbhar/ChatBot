package com.monocept.chatbot.service.impl;

import com.monocept.chatbot.entity.Option;
import com.monocept.chatbot.model.dto.NameIconDto;
import com.monocept.chatbot.model.request.OptionPlaceholderRequest;
import com.monocept.chatbot.model.request.UpdationAcknowledgmentResponse;
import com.monocept.chatbot.reposiotry.OptionRepository;
import com.monocept.chatbot.service.OptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OptionServiceImpl implements OptionService {

    private final OptionRepository optionRepository;

    public OptionServiceImpl(OptionRepository optionRepository) {
        this.optionRepository = optionRepository;
    }

    @Override
    public List<NameIconDto> getAllOptions() {log.info("getAllOptions: Start fetching all option names");
       return optionRepository.findOptionNames();

    }

    @Override
    public List<Option> addOptions(OptionPlaceholderRequest optionPlaceholderRequest) {
        log.info("addOptions: Start adding new options: {}", optionPlaceholderRequest);

        List<Option> options = optionPlaceholderRequest.getData()
                .stream()
                .map(data -> Option.builder()
                        .name(data.getName())
                        .icon(data.getIcon())
                        .build())
                .collect(Collectors.toList());

        List<Option> savedOptions = optionRepository.saveAll(options);
        log.info("addOptions: Successfully added {} options", savedOptions.size());
        return savedOptions;
    }

    @Override
    public String updateOption(String name) {
        log.info("updateOption: Start updating option with name: {}", name);
        int rowsAffected = optionRepository.updatePlaceholderByName(name);
        if (rowsAffected == 0) {
            log.info("updateOption: No option found with name '{}' to update", name);
            return "No matching option found for update.";
        } else {
            log.info("updateOption: Successfully updated option with name '{}'", name);
            return "Option updated successfully.";
        }
    }

    @Override
    public String deleteOption(String name) {
        log.info("deleteOption: Start deleting option with name: {}", name);
        int rowsAffected = optionRepository.deleteByPlaceholderName(name);
        if (rowsAffected == 0) {
            log.warn("deleteOption: No option found with name '{}' to delete", name);
            return "No matching option found for deletion.";
        } else {
            log.info("deleteOption: Successfully deleted option with name '{}'", name);
            return "Option deleted successfully.";
        }
    }

    @Override
    public UpdationAcknowledgmentResponse<Option> optionDataHandler(OptionPlaceholderRequest request) {
        log.info("optionDataHandler: Start handling option data update");

        log.info("optionDataHandler: Deleting all existing options");
        optionRepository.deleteAllOptions();

        List<Option> options = this.addOptions(request);
        log.info("optionDataHandler: Successfully added {} new option records", options.size());

        return new UpdationAcknowledgmentResponse<>(options.size(), options);
    }
}
