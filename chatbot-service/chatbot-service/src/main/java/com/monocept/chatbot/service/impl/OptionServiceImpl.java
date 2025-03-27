package com.monocept.chatbot.service.impl;

import com.monocept.chatbot.entity.Option;
import com.monocept.chatbot.model.request.OptionPlaceholderRequest;
import com.monocept.chatbot.reposiotry.OptionRepository;
import com.monocept.chatbot.service.OptionService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


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
        List<Option> list = options.stream().map(s -> Option.builder().name(s).build()).toList();
        return optionRepository.saveAll(list);
    }

    @Override
    public String updateOption(String name) {
        return optionRepository.deleteByOptionName(name) == 0
                ? "No matching record found to update."
                : "Record  updated.";
    }

    @Override
    public String deleteOption(String name) {
        return optionRepository.deleteByOptionName(name) == 0
                ? "No matching record found to update."
                : "Record  deleted.";
    }

    @Override
    public Object optionDataHandler(OptionPlaceholderRequest request)  {
        return switch (request.getMethodType().name().toLowerCase()) {
            case "add" -> this.addOptions(request.getName());
            case "update" -> processOptions(request.getName(), this::updateOption);
            case "delete" -> processOptions(request.getName(), this::deleteOption);
            default -> throw  new NoSuchElementException("Invalid option perform");
        };
    }

    private Map<String, String> processOptions(List<String> options, Function<String, String> action) {
        return options.stream().collect(Collectors.toMap(option -> option, action));
    }

}
