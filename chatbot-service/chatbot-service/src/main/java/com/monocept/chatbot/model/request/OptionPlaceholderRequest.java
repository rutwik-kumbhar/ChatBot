package com.monocept.chatbot.model.request;


import com.monocept.chatbot.enums.MethodType;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;


@Data
public class OptionPlaceholderRequest {


    private List<String> names;

    private List<UpdateOptionPlaceholderRequest> updateRequest;

    @NotNull(message = "Method type cannot be null")
    private MethodType methodType;
}
