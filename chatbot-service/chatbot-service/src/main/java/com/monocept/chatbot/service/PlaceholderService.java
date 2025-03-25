package com.monocept.chatbot.service;

import com.monocept.chatbot.entity.PlaceHolder;

import java.util.List;

public interface PlaceholderService {

    List<String> getAllPlaceholders();
    PlaceHolder addPlaceholder();
    PlaceHolder deletePlaceholder();
}
