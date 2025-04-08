package com.monocept.chatbot.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;


@Data
@AllArgsConstructor
public class MasterResponse<T>{
    private String status;
    private  int statusCode;
    private String message;
    private  T data;
}
