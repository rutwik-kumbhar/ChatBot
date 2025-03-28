package com.monocept.chatbot.model.response;


import com.monocept.chatbot.entity.Option;
import com.monocept.chatbot.entity.PlaceHolder;
import com.monocept.chatbot.enums.StatusFlag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@AllArgsConstructor
@Builder
@Data
public class UserConfigResponse {
    private UserInfo user;
    private List<String> options;
    private List<String> placeHolders;
    private String botName;
    private StatusFlag statusFlag;
    private String dateTime;

}
