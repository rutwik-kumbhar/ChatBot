package com.monocept.chatbot.controller;

import com.monocept.chatbot.model.dto.ReceiveMessageDTO;
import com.monocept.chatbot.model.request.ReceiveMessageRequest;
import com.monocept.chatbot.model.request.SendMessageRequest;
import com.monocept.chatbot.model.response.MasterResponse;
import com.monocept.chatbot.model.response.ReceiveMessageResponse;
import com.monocept.chatbot.model.response.SendMessageResponse;
import com.monocept.chatbot.service.MessageService;
import com.monocept.chatbot.utils.BotUtility;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chatbot")
public class MessageController {

    private final MessageService messageService;
    private final ModelMapper modelMapper;
    private final  BotUtility botUtility;

    public MessageController(MessageService messageService, ModelMapper modelMapper, BotUtility botUtility) {
        this.messageService = messageService;
        this.modelMapper = modelMapper;
        this.botUtility = botUtility;
    }

    @PostMapping("/send-message")
    public ResponseEntity<MasterResponse<SendMessageResponse>> sendMessage(@RequestBody SendMessageRequest request) {
        ReceiveMessageRequest receiveMessageRequest = botUtility.getBotResponse(request);
        this.sendMessageToUser(receiveMessageRequest);
        SendMessageResponse sendMessageResponse =  messageService.processMessage(request);
        MasterResponse<SendMessageResponse> response = new MasterResponse<>("success",HttpStatus.OK.value(),"Messages sent successfully.", sendMessageResponse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/message-webhook")
    public ResponseEntity<MasterResponse<ReceiveMessageResponse>> receiveMessage(@RequestBody ReceiveMessageRequest request) {
        ReceiveMessageDTO requestDto = modelMapper.map(request, ReceiveMessageDTO.class);
        ReceiveMessageResponse receiveMessageResponse =  messageService.receiveMessage(requestDto);
        MasterResponse<ReceiveMessageResponse> response = new MasterResponse<>("success",HttpStatus.OK.value(),"Messages sent successfully.", receiveMessageResponse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public void sendMessageToUser(ReceiveMessageRequest request) {
        ReceiveMessageDTO requestDto = modelMapper.map(request, ReceiveMessageDTO.class);
        ReceiveMessageResponse receiveMessageResponse =  messageService.receiveMessage(requestDto);
        MasterResponse<ReceiveMessageResponse> response = new MasterResponse<>("success",HttpStatus.OK.value(),"Messages sent successfully.", receiveMessageResponse);
    }


}
