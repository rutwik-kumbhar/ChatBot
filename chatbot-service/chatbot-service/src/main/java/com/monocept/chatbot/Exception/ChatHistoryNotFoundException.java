package com.monocept.chatbot.Exception;

public class ChatHistoryNotFoundException extends RuntimeException {
    public ChatHistoryNotFoundException(String message) {
        super(message);
    }
}
