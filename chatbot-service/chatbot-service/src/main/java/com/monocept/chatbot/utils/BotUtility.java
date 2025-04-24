package com.monocept.chatbot.utils;

import com.monocept.chatbot.controller.MessageController;
import com.monocept.chatbot.enums.BotCommunicationFlow;
import com.monocept.chatbot.enums.MessageTo;
import com.monocept.chatbot.model.dto.MediaDto;
import com.monocept.chatbot.model.request.ReceiveMessageRequest;
import com.monocept.chatbot.model.request.SendMessageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BotUtility {




    @Async("taskExecutor")
    public  ReceiveMessageRequest getBotResponse(SendMessageRequest request){
        Map<String, String> message = new LinkedHashMap<>();

        message.put("Hi", "Hello! 👋 Welcome to MaxSecure Insurance Bot.\nHow can I assist you today?\n1. 📜 View Policy Details\n2. 💰 Check Premium Due\n3. 🧾 Claim Status\n4. 👨‍💼 Talk to Advisor");

        message.put("1", "Sure! Please enter your Policy Number.");

        message.put("POL123456789", "Here are your policy details:\n- Policy Type: Life Insurance\n- Start Date: 12-Jan-2023\n- Status: ✅ Active\n- Premium Due: ₹10,000 on 12-Jan-2025");

        message.put("Check premium", "Your next premium of ₹10,000 is due on 12-Jan-2025. Would you like to:\n1. 💳 Pay Now\n2. 🔔 Set Reminder");

        message.put("Pay Now", "Redirecting to payment gateway... 💳");

        message.put("Set Reminder", "Reminder set! We will notify you 7 days before the due date. ✅");

        message.put("2", "Please provide your Policy Number to check the premium.");

        message.put("Claim", "Sure! Please provide your claim reference number.");

        message.put("CLM987654", "Claim Status:\n- Claim ID: CLM987654\n- Status: ✅ Approved\n- Amount: ₹25,000\n- Processed Date: 03-Mar-2025");

        message.put("3", "Please enter your Claim ID to fetch claim status.");

        message.put("4", "Connecting you to our insurance advisor... 👨‍💼 Please wait.");

        message.put("Talk to agent", "You're now being connected to a live representative. 💬");

        message.put("Thanks", "You're welcome! 😊 Anything else I can help you with?");

        message.put("No", "Thank you for using MaxSecure Insurance Bot. Have a great day! 👋");

        message.put("default", "I'm sorry, I didn't quite catch that. Please choose an option from the menu.");


        String botMessage  = message.get(request.getText());

        return  createSendMessageObject(request , botMessage);

    }

    public ReceiveMessageRequest createSendMessageObject(SendMessageRequest sendMessageRequest , String botMessage){

                // Sample media details
                MediaDto.MediaDetail imageDetail = new MediaDto.MediaDetail("jpg", Arrays.asList("https://example.com/image1.jpg"));
                MediaDto.MediaDetail videoDetail = new MediaDto.MediaDetail("mp4", Arrays.asList("https://example.com/video1.mp4"));
                MediaDto.MediaDetail docDetail = new MediaDto.MediaDetail("pdf", Arrays.asList("https://example.com/doc1.pdf"));

                // MediaDto using builder
                MediaDto mediaDto = MediaDto.builder()
                        .image(Arrays.asList(imageDetail))
                        .video(Arrays.asList(videoDetail))
                        .document(Arrays.asList(docDetail))
                        .build();

                // Acknowledgement using builder
                ReceiveMessageRequest.Acknowledgement ack = ReceiveMessageRequest.Acknowledgement.builder()
                        .status("RECEIVED")
                        .build();

                // Message using builder
                ReceiveMessageRequest.Message message = ReceiveMessageRequest.Message.builder()
                        .text(botMessage)
                        .botOption(true)
                        .media(mediaDto)
                        .acknowledgement(ack)
                        .build();

                // Entry using builder
                ReceiveMessageRequest.Entry entry = ReceiveMessageRequest.Entry.builder()
                        .messageTo(MessageTo.USER)
                        .replyToMessageId("")
                        .message(message)
                        .build();

                // Final ReceiveMessageRequest using builder
                ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                        .emailId(sendMessageRequest.getMessageId())
                        .userId(sendMessageRequest.getUserId())
                        .status(BotCommunicationFlow.COACH)
                        .platform("MSPACE")
                        .isConversationEnded(false)
                        .entry(entry)
                        .build();

                return  request;

            }



}
