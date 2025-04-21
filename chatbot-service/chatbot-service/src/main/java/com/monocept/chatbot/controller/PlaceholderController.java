package com.monocept.chatbot.controller;


import com.monocept.chatbot.entity.PlaceHolder;
import com.monocept.chatbot.enums.Status;
import com.monocept.chatbot.model.request.OptionPlaceholderRequest;
import com.monocept.chatbot.model.request.UpdationAcknowledgmentResponse;
import com.monocept.chatbot.model.response.MasterResponse;
import com.monocept.chatbot.service.PlaceholderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/chatbot")
public class PlaceholderController {


    private final PlaceholderService placeholderService;

    public PlaceholderController(PlaceholderService placeholderService) {
        this.placeholderService = placeholderService;
    }

    @PostMapping("/placeholder")
    public ResponseEntity<MasterResponse<Object>> optionDataHandler(@RequestBody @Valid OptionPlaceholderRequest request){
        MasterResponse<Object> response;
        try {
            UpdationAcknowledgmentResponse<PlaceHolder> object = placeholderService.placeholderDataHandler(request);
            String message =  object.getTotalUpdated() > 0 ? "Records updated successfully." : "No records found to updated.";
            response = new MasterResponse<>(Status.SUCCESS.name(), HttpStatus.OK.value(), message, object );
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }catch (Exception exception){
            response = new MasterResponse<>(Status.FAILURE.name(),HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage(),  null );
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }
}
