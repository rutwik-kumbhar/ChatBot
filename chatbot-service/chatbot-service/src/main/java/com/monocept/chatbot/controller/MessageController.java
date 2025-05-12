package com.monocept.chatbot.controller;

import com.monocept.chatbot.model.dto.ReceiveMessageDTO;
import com.monocept.chatbot.model.request.MessageCountRequest;
import com.monocept.chatbot.model.request.ReceiveMessageRequest;
import com.monocept.chatbot.model.response.MasterResponse;
import com.monocept.chatbot.model.response.ReceiveMessageResponse;
import com.monocept.chatbot.service.MessageService;
import com.monocept.chatbot.utils.AppConstant;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
public class MessageController {

    private final MessageService messageService;
    private final ModelMapper modelMapper;

    public MessageController(MessageService messageService, ModelMapper modelMapper) {
        this.messageService = messageService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/message-webhook")
    public ResponseEntity<MasterResponse<ReceiveMessageResponse>> receiveMessage(@RequestBody ReceiveMessageRequest request) {
        ReceiveMessageDTO requestDto = modelMapper.map(request, ReceiveMessageDTO.class);
        ReceiveMessageResponse receiveMessageResponse =  messageService.receiveMessage(requestDto);
        MasterResponse<ReceiveMessageResponse> response = new MasterResponse<>("success",HttpStatus.OK.value(),"Messages sent successfully.", receiveMessageResponse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping("/message/unread-count")
    public  ResponseEntity<MasterResponse<?>>  getUnreadMessageCount(@RequestBody MessageCountRequest request){
        MasterResponse<?> response;
        try {
            Map<String, Long> messageCountData  = messageService.getMessageCountSendByBot(request);
            response = new MasterResponse<>(AppConstant.SUCCESS , HttpStatus.OK.value(), "Unread message count fetched successfully." , messageCountData);
            return  new ResponseEntity<>( response, HttpStatus.OK);
        }catch (Exception e){
            response = new MasterResponse<>(AppConstant.FAILURE , HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage() , null);
            return  new ResponseEntity<>( response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
