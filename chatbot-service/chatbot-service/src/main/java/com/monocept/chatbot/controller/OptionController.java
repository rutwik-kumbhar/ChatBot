package com.monocept.chatbot.controller;


import com.monocept.chatbot.entity.Option;
import com.monocept.chatbot.enums.Status;
import com.monocept.chatbot.model.request.OptionPlaceholderRequest;
import com.monocept.chatbot.model.request.UpdationAcknowledgmentResponse;
import com.monocept.chatbot.model.response.MasterResponse;
import com.monocept.chatbot.service.OptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api")
public class OptionController {

    private final  OptionService optionService;

    public OptionController(OptionService optionService) {
        this.optionService = optionService;
    }


    @PostMapping("/option")
    public ResponseEntity<MasterResponse<Object>> optionDataHandler(@RequestBody @Valid OptionPlaceholderRequest request){
        log.info("optionDataHandler : start {}" , request);
        MasterResponse<Object> response;
            try {
                UpdationAcknowledgmentResponse<Option> object = optionService.optionDataHandler(request);
                String message =  object.getTotalUpdated() > 0 ? "Records updated successfully." : "No records found to updated.";
                response = new MasterResponse<>(Status.SUCCESS.name(),HttpStatus.OK.value(), message, object );
                 return ResponseEntity.status(HttpStatus.OK).body(response);
            }catch (Exception exception){
                response = new MasterResponse<>(Status.FAILURE.name(),HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage(),  null );
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
    }



}
