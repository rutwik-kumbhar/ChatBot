package com.monocept.chatbot.service.impl;

import com.monocept.chatbot.entity.Option;
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
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@Service
public class OptionServiceImpl implements OptionService {


    private final OptionRepository optionRepository;

    public OptionServiceImpl(OptionRepository optionRepository) {
        this.optionRepository = optionRepository;
    }

    @Override
    public List<String> getAllOptions() {
        return optionRepository.findOptionNames();
    }

    @Override
    public List<Option> addOptions(List<String> options) {
        log.info("addOptions : start {}" , options);
        List<Option> list = options.stream().map(s -> Option.builder().name(s).build()).toList();
        log.info("addOptions : options added data {}" , list);
        return optionRepository.saveAll(list);
    }

    @Override
    public UpdateOptionPlaceholderResponse updateOption(List<UpdateOptionPlaceholderRequest> updateRequest) {
        log.info("updateOption : start {}" , updateRequest);
        int totalRecords = updateRequest.size();

        Map<Boolean, List<UpdateOptionPlaceholderRequest>> result = updateRequest.stream()
                .collect(Collectors.partitioningBy(update ->
                        optionRepository.updateOptionByName(update.getNewValue(), update.getOldValue()) > 0
                ));

        log.info("updateOption : updated result {}" , result);

        List<String> updatedRecords = result.get(true).stream()
                .map(UpdateOptionPlaceholderRequest::getOldValue)
                .toList();

        log.info("updateOption : updated records {}" , updatedRecords);

        List<String> unupdatedRecords = result.get(false).stream()
                .map(UpdateOptionPlaceholderRequest::getOldValue)
                .toList();

        log.info("updateOption : unupdated records {}" , unupdatedRecords);

        int totalUpdated = updatedRecords.size();
        int totalUnupdated = unupdatedRecords.size();

        log.info("updateOption : updated records count {}" , totalUpdated);
        log.info("updateOption : unupdated records count {}" , totalUnupdated);

        return new UpdateOptionPlaceholderResponse(totalRecords, totalUpdated, totalUnupdated, updatedRecords, unupdatedRecords);
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
    public UpdationAcknowledgmentResponse<Option> updateOptionsHandler(List<String> names) {
        optionRepository.deleteAllOptions();
        List<Option> options = this.addOptions(names);
        return new UpdationAcknowledgmentResponse<>(options.size(),  options);
    }

    public Object optionDataHandler(OptionPlaceholderRequest request)  {
        return switch (request.getMethodType().name().toLowerCase()) {
            case "add" -> this.addOptions(request.getNames());
            case "update" -> this.updateOption(request.getUpdateRequest());
            case "delete" -> this.deleteOption(request.getNames());
            default -> throw  new NoSuchElementException("Invalid option perform");
        };
    }


}
