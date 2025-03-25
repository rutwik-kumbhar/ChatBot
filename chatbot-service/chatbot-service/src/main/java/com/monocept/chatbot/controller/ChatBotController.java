package com.monocept.chatbot.controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.monocept.chatbot.ChatDTO.HistoryDTO;
import com.monocept.chatbot.Entity.History;
import com.monocept.chatbot.MasterResponse.MasterResponse;
import com.monocept.chatbot.Request.MessageRequest;
import com.monocept.chatbot.service.ChatHistoryService;
import com.monocept.chatbot.service.ReceivedMessageSocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/chatbot")
public class ChatBotController {
    private static final Logger logger = LoggerFactory.getLogger(ChatBotController.class);
    private final ReceivedMessageSocketService receivedMessageSocketService;
    private final ChatHistoryService chatHistoryService;
    // Constructor Injection for both services
    public ChatBotController(ReceivedMessageSocketService receivedMessageSocketService, ChatHistoryService chatHistoryService) {
        this.receivedMessageSocketService = receivedMessageSocketService;
        this.chatHistoryService = chatHistoryService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> receiveWebhook(@RequestBody Object message) throws JsonProcessingException {
         receivedMessageSocketService.receive(message);
         return  new ResponseEntity<>("Message Send to Webhook Successfully", HttpStatus.OK);
    }

    @PostMapping ("/messages/last3months")
    public ResponseEntity<MasterResponse<List<HistoryDTO>>> getMessagesFromLast90Days(@RequestBody MessageRequest messageRequest) {
        try {
            logger.info("Received request to fetch messages for Email: {}", messageRequest.getEmail());
            String email = messageRequest.getEmail();
            // Call to service layer to get messages from the last 90 days
            List<HistoryDTO> messages = chatHistoryService.getMessagesFromLast90Days(email);
            MasterResponse<List<HistoryDTO>> response =null;
            if (messages != null && !messages.isEmpty()) {
                // Success case, messages found
                response = new MasterResponse<>("success", "Messages retrieved successfully.", messages);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response = new MasterResponse<>("failure", "No messages found in the last 90 days.", null);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (Exception ex) {
            logger.error("Error occurred while fetching messages: {}", ex.getMessage(), ex);
            MasterResponse<List<HistoryDTO>> errorResponse = new MasterResponse<>("error", "An unexpected error occurred.", null);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

