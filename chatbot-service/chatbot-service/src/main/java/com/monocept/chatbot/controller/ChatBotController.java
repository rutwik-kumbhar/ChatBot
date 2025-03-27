package com.monocept.chatbot.controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.monocept.chatbot.model.dto.HistoryDTO;
import com.monocept.chatbot.model.request.MessageRequest;
import com.monocept.chatbot.model.response.MasterResponse;
import com.monocept.chatbot.service.ChatHistoryDataPurge;
import com.monocept.chatbot.service.ChatHistoryService;
//import com.monocept.chatbot.service.ReceivedMessageSocketService;
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
    //private static final Logger logger = LoggerFactory.getLogger(ChatBotController.class);
//    private final ReceivedMessageSocketService receivedMessageSocketService;
   // private final ChatHistoryService chatHistoryService;
   // private final ChatHistoryDataPurge chatHistoryDataPurge;

//    public ChatBotController(ReceivedMessageSocketService receivedMessageSocketService, ChatHistoryService chatHistoryService, ChatHistoryDataPurge chatHistoryDataPurge) {
//        this.receivedMessageSocketService = receivedMessageSocketService;
//        this.chatHistoryService = chatHistoryService;
//        this.chatHistoryDataPurge = chatHistoryDataPurge;
//    }
    //public ChatBotController( ChatHistoryService chatHistoryService, ChatHistoryDataPurge chatHistoryDataPurge) {

      //  this.chatHistoryService = chatHistoryService;
        //this.chatHistoryDataPurge = chatHistoryDataPurge;
    //}

    // Constructor Injection for both services
  //  public ChatBotController(ReceivedMessageSocketService receivedMessageSocketService, ChatHistoryService chatHistoryService) {
    //    this.receivedMessageSocketService = receivedMessageSocketService;
      //  this.chatHistoryService = chatHistoryService;
   // }

//    @PostMapping("/webhook")
//    public ResponseEntity<String> receiveWebhook(@RequestBody Object message) throws JsonProcessingException {
//         receivedMessageSocketService.receive(message);
//         return  new ResponseEntity<>("Message Send to Webhook Successfully", HttpStatus.OK);
//    }

  /*  @PostMapping ("/messages")
    public ResponseEntity<MasterResponse<Page<HistoryDTO>>> getMessagesFromLast90Days(@RequestBody MessageRequest messageRequest) {
        try {
            logger.info("Received request to fetch messages for Email: {}", messageRequest.getEmail());
            String email = messageRequest.getEmail();
            // Call to service layer to get messages from the last 90 days
            Page<HistoryDTO> messages = chatHistoryService.getMessagesFromLast90Days(email,messageRequest.page,messageRequest.size);
            MasterResponse<Page<HistoryDTO>> response;
            if (messages != null && !messages.isEmpty()) {
                // Success case, messages found
                response = new MasterResponse<>("success", 200,"Messages retrieved successfully.", messages);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response = new MasterResponse<>("failure", 201,"No messages found in the last 90 days.", null);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (Exception ex) {
            logger.error("Error occurred while fetching messages: {}", ex.getMessage(), ex);
            MasterResponse<Page<HistoryDTO>> errorResponse = new MasterResponse<>("error", 401,"An unexpected error occurred.", null);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }*/
// only for testing
   // @GetMapping("/cleanup-data")
    //public String cleanupData() {
      //  chatHistoryDataPurge.deleteHistoryData90Days();
     //   return "Data cleanup triggered.";
   // }

}

