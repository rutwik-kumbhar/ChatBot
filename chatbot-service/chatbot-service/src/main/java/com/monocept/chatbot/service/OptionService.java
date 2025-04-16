package com.monocept.chatbot.service;

import com.monocept.chatbot.entity.Option;
import com.monocept.chatbot.model.dto.NameIconDto;
import com.monocept.chatbot.model.request.OptionPlaceholderRequest;
import com.monocept.chatbot.model.request.UpdationAcknowledgmentResponse;
import com.monocept.chatbot.model.response.DeleteOptionResponse;

import java.util.List;

public interface OptionService {

    List<NameIconDto> getAllOptions();
    List<Option> addOptions(OptionPlaceholderRequest request);
    DeleteOptionResponse deleteOption(List<String> name);
    UpdationAcknowledgmentResponse<Option> optionDataHandler(OptionPlaceholderRequest request);


}
