package com.monocept.chatbot.controller;

import com.monocept.chatbot.model.ChatHistory;
import com.monocept.chatbot.service.ChatHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatHistoryController {

    private final ChatHistoryService chatHistoryService;

    public ChatHistoryController(ChatHistoryService chatHistoryService) {
        this.chatHistoryService = chatHistoryService;
    }

    // Fetch chat history by email
    @GetMapping("/history/email/{email}")
    public ResponseEntity<List<ChatHistory>> getChatByEmail(@PathVariable String email) {
        return ResponseEntity.ok(chatHistoryService.getChatHistoryByEmail(email));
    }
}
