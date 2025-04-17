package com.monocept.chatbot.service;

import com.monocept.chatbot.model.request.UserInfo;

public interface CognitoService {
    String getCognitoToken(UserInfo userInfo);
}
