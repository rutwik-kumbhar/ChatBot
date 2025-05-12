package com.monocept.chatbot.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UpdateOptionPlaceholderResponse {

    private int totalRecords;
    private int totalUpdatedRecords;
    private int totalUnupdatedRecords;
    private List<String> updatedRecords;
    private List<String> unupdatedRecords;
}
