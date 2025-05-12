package com.monocept.chatbot.model.response;

import com.monocept.chatbot.enums.Status;
import lombok.*;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MasterResponse<T>{
    private String status;
    private  int statusCode;
    private String message;
    private  T data;
}
