package com.monocept.chatbot.model.response;


import com.monocept.chatbot.entity.Option;
import com.monocept.chatbot.entity.PlaceHolder;
import com.monocept.chatbot.enums.StatusFlag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Builder
@Data
public class UserConfigResponse {
    private Map<String,Object> userInfo;
    private List<String> options;
    private List<String> placeHolders;
    private String botName;
    private StatusFlag statusFlag;
    private String dateTime;

}
