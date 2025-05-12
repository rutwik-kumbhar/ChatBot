package com.monocept.chatbot.exceptions;

public class RequestEncryptionException extends RuntimeException {
    public RequestEncryptionException() {
    }

    public RequestEncryptionException(String message) {
        super(message);
    }

    public RequestEncryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
