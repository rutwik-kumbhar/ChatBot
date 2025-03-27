package com.monocept.chatbot.controller;


import com.monocept.chatbot.enums.Status;
import com.monocept.chatbot.model.request.OptionPlaceholderRequest;
import com.monocept.chatbot.model.response.MasterResponse;
import com.monocept.chatbot.service.OptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class OptionController {

    private final  OptionService optionService;

    public OptionController(OptionService optionService) {
        this.optionService = optionService;
    }


    @PostMapping("/option")
    public ResponseEntity<MasterResponse<Object>> optionDataHandler(OptionPlaceholderRequest request){
        MasterResponse<Object> response;
            try {
                 Object object = optionService.optionDataHandler(request);
                 response = new MasterResponse<>(Status.SUCCESS.name(),HttpStatus.OK.value(), Status.SUCCESS.name(), object );
                 return ResponseEntity.status(HttpStatus.OK).body(response);
            }catch (Exception exception){
                response = new MasterResponse<>(Status.FAILURE.name(),HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage(),  null );
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
    }



}
