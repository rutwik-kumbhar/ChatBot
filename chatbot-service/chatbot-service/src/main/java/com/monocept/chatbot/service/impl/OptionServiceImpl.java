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
    public List<Option> getAllOptions() {
        return optionRepository.findByActiveTrue();
    }

}
