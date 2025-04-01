package com.monocept.chatbot.service;

import com.monocept.chatbot.entity.Option;
import com.monocept.chatbot.model.request.OptionPlaceholderRequest;
import com.monocept.chatbot.model.request.UpdateOptionPlaceholderRequest;
import com.monocept.chatbot.model.request.UpdationAcknowledgmentResponse;
import com.monocept.chatbot.model.response.DeleteOptionResponse;
import com.monocept.chatbot.model.response.UpdateOptionPlaceholderResponse;

import java.util.List;

public interface OptionService {

    List<String> getAllOptions();
    List<Option> addOptions(List<String> options);
    UpdateOptionPlaceholderResponse updateOption(List<UpdateOptionPlaceholderRequest> updateRequest);
    DeleteOptionResponse deleteOption(List<String> name);
    Object optionDataHandler(OptionPlaceholderRequest request);
    UpdationAcknowledgmentResponse<Option> updateOptionsHandler(List<String> names);


}
