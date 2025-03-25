package com.monocept.chatbot.controller;


import com.monocept.chatbot.enums.Status;
import com.monocept.chatbot.exceptions.ResourcesNotFoundException;
import com.monocept.chatbot.model.request.GetUserConfigRequest;
import com.monocept.chatbot.model.response.MasterResponse;
import com.monocept.chatbot.model.response.UserConfigResponse;
import com.monocept.chatbot.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1")
public class UserController {


    private final  UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user/config")
    public ResponseEntity<MasterResponse<UserConfigResponse>> getUserConfig(@RequestBody GetUserConfigRequest request) {
        try {
            UserConfigResponse userConfiguration = userService.getUserConfiguration(request);
            MasterResponse<UserConfigResponse> response = new MasterResponse<>(
                    Status.SUCCESS.name(),
                    HttpStatus.OK.value(),
                    "User config fetched successfully",
                    userConfiguration
            );
            return ResponseEntity.ok(response);
        } catch (ResourcesNotFoundException exception) {
            MasterResponse<UserConfigResponse> response = new MasterResponse<>(
                    Status.FAILURE.name(),
                    HttpStatus.NOT_FOUND.value(),
                    exception.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception exception) {
            MasterResponse<UserConfigResponse> response = new MasterResponse<>(
                    Status.FAILURE.name(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    exception.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }




}
