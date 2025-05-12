package com.monocept.chatbot.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class UpdationAcknowledgmentResponse<T> {
    private int totalUpdated;
    private List<T> records;
}
