package com.monocept.chatbot.handler;

import com.monocept.chatbot.model.request.OptionPlaceholderRequest;
import com.monocept.chatbot.service.OptionService;
import com.monocept.chatbot.service.PlaceholderService;
import lombok.Data;
import org.springframework.stereotype.Service;

@Service
public class OptionPlaceholderHandler {

    private final OptionService optionService;
    private  final PlaceholderService placeholderService;


    public OptionPlaceholderHandler(OptionService optionService, PlaceholderService placeholderService) {
        this.optionService = optionService;
        this.placeholderService = placeholderService;
    }

    public  void dataHandler(OptionPlaceholderRequest request){

    }
}
