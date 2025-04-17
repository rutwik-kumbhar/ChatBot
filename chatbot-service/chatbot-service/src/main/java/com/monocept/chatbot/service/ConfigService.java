package com.monocept.chatbot.service;

import com.monocept.chatbot.model.request.GetUserConfigRequest;
import com.monocept.chatbot.model.response.UserConfigResponse;

public interface ConfigService {
    UserConfigResponse getConfiguration(GetUserConfigRequest request);
}
