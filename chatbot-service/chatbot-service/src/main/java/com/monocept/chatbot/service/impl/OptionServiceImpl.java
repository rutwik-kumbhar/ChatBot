package com.monocept.chatbot.service.impl;

import com.monocept.chatbot.entity.Option;
import com.monocept.chatbot.model.dto.NameIconDto;
import com.monocept.chatbot.model.request.OptionPlaceholderRequest;
import com.monocept.chatbot.model.request.UpdateOptionPlaceholderRequest;
import com.monocept.chatbot.model.request.UpdationAcknowledgmentResponse;
import com.monocept.chatbot.model.response.DeleteOptionResponse;
import com.monocept.chatbot.model.response.UpdateOptionPlaceholderResponse;
import com.monocept.chatbot.reposiotry.OptionRepository;
import com.monocept.chatbot.service.OptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
public class OptionServiceImpl implements OptionService {


    private final OptionRepository optionRepository;

    public OptionServiceImpl(OptionRepository optionRepository) {
        this.optionRepository = optionRepository;
    }

    @Override
    public List<NameIconDto> getAllOptions() {
        return optionRepository.findOptionNames();
    }

    @Override
    public List<Option> addOptions(OptionPlaceholderRequest optionPlaceholderRequest) {
        log.info("addOptions : start {}" , optionPlaceholderRequest);
        List<Option> options =  optionPlaceholderRequest.getData()
                .stream()
                .map(data ->
                        Option.builder()
                                .name(data.getName())
                                .icon(data.getIcon())
                                .build())
                .collect(Collectors.toList());
        log.info("addOptions : options added data {}" , options);
        return optionRepository.saveAll(options);
    }


    @Override
    public DeleteOptionResponse deleteOption(List<String> name) {
        log.info("deleteOption : start {} ", name);
        int deletedCount = optionRepository.deleteByOptionNames(name);

        // Return response based on whether any records were deleted
        return new DeleteOptionResponse(
                name.size(),
                deletedCount > 0 ? List.of(String.valueOf(name)) : List.of(),
                deletedCount > 0 ? "Records deleted successfully." : "No matching records found to delete."
        );
    }

    @Override
    public UpdationAcknowledgmentResponse<Option> optionDataHandler(OptionPlaceholderRequest request) {
        optionRepository.deleteAllOptions();
        List<Option> options = this.addOptions(request);
        return new UpdationAcknowledgmentResponse<>(options.size(),  options);
    }



}
