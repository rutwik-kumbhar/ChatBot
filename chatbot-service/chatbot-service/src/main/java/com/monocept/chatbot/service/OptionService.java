package com.monocept.chatbot.service;

import com.monocept.chatbot.entity.Option;
import com.monocept.chatbot.model.request.OptionPlaceholderRequest;

import java.util.List;

public interface OptionService {

    List<String> getAllOptions();
    List<Option> addOptions(List<String> options);
    String updateOption(String name);
    String  deleteOption(String name);
    Object optionDataHandler(OptionPlaceholderRequest request);


}
