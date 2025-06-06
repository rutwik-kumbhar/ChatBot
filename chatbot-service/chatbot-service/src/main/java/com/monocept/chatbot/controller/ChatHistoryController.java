package com.monocept.chatbot.controller;

import com.monocept.chatbot.model.dto.MessageDto;
import com.monocept.chatbot.model.request.MessageRequest;
import com.monocept.chatbot.model.response.MasterResponse;
import com.monocept.chatbot.service.ChatHistoryDataPurge;
import com.monocept.chatbot.service.ChatHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Validated
@RestController
@RequestMapping("/api/chatbot")
public class ChatHistoryController {
    private static final Logger logger = LoggerFactory.getLogger(ChatHistoryController.class);
    private final ChatHistoryService chatHistoryService;
    private final ChatHistoryDataPurge chatHistoryDataPurge;

    public ChatHistoryController(ChatHistoryService chatHistoryService, ChatHistoryDataPurge chatHistoryDataPurge) {
        this.chatHistoryService = chatHistoryService;
        this.chatHistoryDataPurge = chatHistoryDataPurge;
    }

    // only for dev testing
    @GetMapping("/cleanup-data")
    public String cleanupData() {
        chatHistoryDataPurge.deleteDataOlderThanNDays();
        return "Data cleanup triggered.";
    }

    @PostMapping("/chathistory")
    public ResponseEntity<MasterResponse<Page<MessageDto>>> chatHistory(@Valid @RequestBody MessageRequest messageRequest) {
        try {
          //  logger.info("Received request to fetch messages for Email: {}", messageRequest.getEmail());
            logger.info("Received request to fetch messages for agentId: {}", messageRequest.agentId);

            // Call to service layer to get messages from the last 90 days
            Page<MessageDto> messages = chatHistoryService.getChatHistory(messageRequest.agentId, messageRequest.page, messageRequest.size);
            MasterResponse<Page<MessageDto>> response;
            if (messages != null && !messages.isEmpty()) {
                // Success case, messages found
                response = new MasterResponse<>("success", HttpStatus.OK.value(), "Messages retrieved successfully.", messages);

                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response = new MasterResponse<>("failure", HttpStatus.INTERNAL_SERVER_ERROR.value(), "No messages found in the last 90 days.", null);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (Exception ex) {
            logger.error("Error occurred while fetching messages: {}", ex.getMessage(), ex);
            MasterResponse<Page<MessageDto>> errorResponse = new MasterResponse<>("error", HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred.", null);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
