package com.monocept.chatbot.service;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface ReceivedMessageSocketService {
    void receive(Object message) throws JsonProcessingException;
}
