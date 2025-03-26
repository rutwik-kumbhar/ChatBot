package com.monocept.chatbot.MasterResponse;


import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MasterResponse<T> {
    private String status;
    private String message;
    private T data;
}
