package com.monocept.chatbot.service.impl;

import com.monocept.chatbot.reposiotry.PlaceholderRepository;
import com.monocept.chatbot.service.PlaceholderService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class PlaceHolderServiceImpl implements PlaceholderService {

    private  final  PlaceholderRepository placeholderRepository;

    public PlaceHolderServiceImpl(PlaceholderRepository placeholderRepository) {
        this.placeholderRepository = placeholderRepository;
    }

    @Override
    public List<String> getAllPlaceholders() {
        return  placeholderRepository.findActivePlaceholderNames();
    }
}
