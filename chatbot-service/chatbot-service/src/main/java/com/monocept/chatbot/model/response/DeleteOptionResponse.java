package com.monocept.chatbot.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DeleteOptionResponse {
    private int totalRecords;
    private List<String> deletedRecords;
    private String message;

}
