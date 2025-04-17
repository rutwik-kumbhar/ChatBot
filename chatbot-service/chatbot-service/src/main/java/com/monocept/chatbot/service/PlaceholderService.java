package com.monocept.chatbot.service;

import com.monocept.chatbot.entity.PlaceHolder;
import com.monocept.chatbot.model.request.OptionPlaceholderRequest;
import com.monocept.chatbot.model.request.UpdationAcknowledgmentResponse;

import java.util.List;

public interface PlaceholderService {

    List<String> getAllPlaceholders();
    List<PlaceHolder> addPlaceholders(List<String> options);
    String updatePlaceholder(String name);
    String  deletePlaceholder(String name);
    Object optionDataHandler(OptionPlaceholderRequest request);
    UpdationAcknowledgmentResponse<PlaceHolder> updatePlaceholderHandler(List<String> names);

}
