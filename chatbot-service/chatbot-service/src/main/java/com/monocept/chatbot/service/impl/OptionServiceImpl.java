package com.monocept.chatbot.service.impl;

import com.monocept.chatbot.entity.Option;
import com.monocept.chatbot.reposiotry.OptionRepository;
import com.monocept.chatbot.service.OptionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OptionServiceImpl implements OptionService {


    private final OptionRepository optionRepository;

    public OptionServiceImpl(OptionRepository optionRepository) {
        this.optionRepository = optionRepository;
    }

    @Override
    public List<String> getAllOptions() {
        return optionRepository.findActiveOptionNames();
    }

    @Override
    public List<Option> addOptions(List<String> options) {
        List<Option> list = options.stream().map(s -> Option.builder().name(s).active(true).build()).toList();
        return optionRepository.saveAll(list);
    }

    @Override
    public String updateOption(String name) {
        return optionRepository.deactivateOptionByName(name) == 0
                ? "No matching record found to update."
                : "Record  updated.";
    }

}
