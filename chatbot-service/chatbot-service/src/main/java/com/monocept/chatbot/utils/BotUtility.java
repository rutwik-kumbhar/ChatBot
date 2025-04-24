package com.monocept.chatbot.utils;

import com.monocept.chatbot.controller.MessageController;
import com.monocept.chatbot.enums.BotCommunicationFlow;
import com.monocept.chatbot.enums.MessageTo;
import com.monocept.chatbot.model.dto.MediaDto;
import com.monocept.chatbot.model.request.ReceiveMessageRequest;
import com.monocept.chatbot.model.request.SendMessageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BotUtility {




    @Async("taskExecutor")
    public  ReceiveMessageRequest getBotResponse(SendMessageRequest request){
        Map<String, String>  message  = new HashMap<>();
        message.put("hi","Hello, how may I help you?");

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
