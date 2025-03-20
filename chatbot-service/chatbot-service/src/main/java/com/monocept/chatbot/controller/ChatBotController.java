package com.monocept.chatbot.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.monocept.chatbot.service.ReceivedMessageSocketService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/chatbot")
public class ChatBotController {


    private final ReceivedMessageSocketService receivedMessageSocketService;

    public ChatBotController(ReceivedMessageSocketService receivedMessageSocketService) {
        this.receivedMessageSocketService = receivedMessageSocketService;
    }

    @PostMapping("/webhook")
    public void receiveWebhook(@RequestBody Object message) throws JsonProcessingException {
         receivedMessageSocketService.receive(message);
    }


}
