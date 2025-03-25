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
    public Option addOptions(List<String> options) {
        return null;
    }

    @Override
    public void deleteOption(String name) {

    }

}
