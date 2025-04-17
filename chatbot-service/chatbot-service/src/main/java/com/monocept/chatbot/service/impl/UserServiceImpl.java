//package com.monocept.chatbot.service.impl;
//
//
//import com.monocept.chatbot.exceptions.ResourcesNotFoundException;
//import com.monocept.chatbot.model.mapper.UserMapper;
//import com.monocept.chatbot.model.request.GetUserConfigRequest;
//import com.monocept.chatbot.model.response.UserConfigResponse;
//import com.monocept.chatbot.model.response.UserInfo;
//import com.monocept.chatbot.service.OptionService;
//import com.monocept.chatbot.service.UserService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.time.ZonedDateTime;
//import java.util.List;
//import java.util.Optional;
//
//
//@Slf4j
//@Service
//public class UserServiceImpl implements UserService {
//
//    private  final  UserRepository userRepository;
//    private final  UserMapper userMapper;
//    private final OptionService optionService;
//    private final PlaceHolderServiceImpl placeHolderService;
//
//
//    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, OptionService optionService, PlaceHolderServiceImpl placeHolderService) {
//        this.userRepository = userRepository;
//        this.userMapper = userMapper;
//        this.optionService = optionService;
//        this.placeHolderService = placeHolderService;
//    }
//
//    @Override
//    public User saveUser(GetUserConfigRequest request) {
//        return  null;
//    }
//
//    @Override
//    public UserConfigResponse getUserConfiguration(GetUserConfigRequest request) {
//
//        String email =  request.getEmail();
//        Optional<User> userOptional = userRepository.findByEmail(email);
//        User user  = userOptional.orElseThrow(() -> new ResourcesNotFoundException("User not found by give email " + email));
//
//        UserInfo userInfo = userMapper.mapToUserInfo(user);
//        List<String> options = optionService.getAllOptions();
//        List<String> placeholders = placeHolderService.getAllPlaceholders();
//
//        log.info("getUserConfiguration : place holders : {} ", placeholders);
//
//        return UserConfigResponse.builder()
//                .user(userInfo)
//                .options(options)
//                .placeHolders(placeholders)
//                .botName("Ely") // Need to fetch later from db
//                .statusFlag(user.getStatusFlag())
//                .dateTime(ZonedDateTime.now().toString()).build();
//
//
//    }
//
//}
