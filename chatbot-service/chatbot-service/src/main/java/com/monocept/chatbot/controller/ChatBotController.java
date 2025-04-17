package com.monocept.chatbot.controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.monocept.chatbot.model.dto.HistoryDTO;
import com.monocept.chatbot.model.request.MessageRequest;
import com.monocept.chatbot.model.response.MasterResponse;
import com.monocept.chatbot.service.ChatHistoryDataPurge;
import com.monocept.chatbot.service.ChatHistoryService;
//import com.monocept.chatbot.service.ReceivedMessageSocketService;
import com.monocept.chatbot.service.ReceivedMessageSocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/chatbot")
public class ChatBotController {
    private static final Logger logger = LoggerFactory.getLogger(ChatBotController.class);


    private final ReceivedMessageSocketService receivedMessageSocketService;

    public ChatBotController(ReceivedMessageSocketService receivedMessageSocketService) {
        this.receivedMessageSocketService = receivedMessageSocketService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> receiveWebhook(@RequestBody Object message) throws JsonProcessingException {
         receivedMessageSocketService.receive(message);
         return  new ResponseEntity<>("Message Send to Webhook Successfully", HttpStatus.OK);
    }



}

