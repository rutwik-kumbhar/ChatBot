package com.monocept.chatbot.model.response;

import com.monocept.chatbot.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
@AllArgsConstructor
public class MasterResponse<R>{
    private String status;
    private  int statusCode;
    private String message;
    private  R data;
}
