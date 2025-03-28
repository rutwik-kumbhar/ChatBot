package com.monocept.chatbot.model.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monocept.chatbot.entity.User;
import com.monocept.chatbot.model.response.UserInfo;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final ObjectMapper objectMapper;

    public UserMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public UserInfo mapToUserInfo(User user) {
        return objectMapper.convertValue(user, UserInfo.class);
    }

    public  User mapToUser(UserInfo userInfo){
        return objectMapper.convertValue(userInfo, User.class);
    }

}
