package com.monocept.chatbot.service;

import com.monocept.chatbot.entity.PlaceHolder;
import com.monocept.chatbot.model.dto.NameIconDto;
import com.monocept.chatbot.model.request.OptionPlaceholderRequest;
import com.monocept.chatbot.model.request.UpdationAcknowledgmentResponse;

import java.util.List;

public interface PlaceholderService {

    List<NameIconDto> getAllPlaceholders();
    List<PlaceHolder> addPlaceholders(OptionPlaceholderRequest optionPlaceholderRequest);
    String updatePlaceholder(String name);
    String  deletePlaceholder(String name);
    UpdationAcknowledgmentResponse<PlaceHolder> placeholderDataHandler(OptionPlaceholderRequest names);

}
