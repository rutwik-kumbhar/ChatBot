package com.monocept.chatbot.model.response;


import com.monocept.chatbot.enums.BotCommunicationFlow;
import com.monocept.chatbot.model.dto.NameIconDto;
import com.monocept.chatbot.model.request.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Builder
@Data
public class UserConfigResponse {
    private UserInfo userInfo;
    private List<NameIconDto> options;
    private List<NameIconDto> placeHolders;
    private String botName;
    private BotCommunicationFlow statusFlag;
    private String dateTime;

}
