package com.monocept.chatbot.exceptions;

public class RequestDecryptionException extends  RuntimeException{

    public RequestDecryptionException() {
        super();
    }

    public RequestDecryptionException(String message) {
        super(message);
    }

    public RequestDecryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
