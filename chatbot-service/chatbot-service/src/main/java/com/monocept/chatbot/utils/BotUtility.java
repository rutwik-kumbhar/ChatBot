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
                MediaDto.MediaDetail imageDetail = new MediaDto.MediaDetail("jpg", Arrays.asList("https://monocepthyd-my.sharepoint.com/:i:/g/personal/sarun_monocept_com/EUqNT3tftzdMu3G0nfISlrgBMaerMi5TnxEOv4_sFUDifQ?e=BNNeSE"));
                MediaDto.MediaDetail videoDetail = new MediaDto.MediaDetail("mp4", Arrays.asList("https://monocepthyd-my.sharepoint.com/:v:/g/personal/sarun_monocept_com/Efu4TMxSuKRPpSVQD_JPF5MBb1CKbAyraxEpcE39dp3rgQ?nav=eyJyZWZlcnJhbEluZm8iOnsicmVmZXJyYWxBcHAiOiJPbmVEcml2ZUZvckJ1c2luZXNzIiwicmVmZXJyYWxBcHBQbGF0Zm9ybSI6IldlYiIsInJlZmVycmFsTW9kZSI6InZpZXciLCJyZWZlcnJhbFZpZXciOiJNeUZpbGVzTGlua0NvcHkifX0&e=53nOqb"));
                MediaDto.MediaDetail docDetail = new MediaDto.MediaDetail("pdf", Arrays.asList("https://monocepthyd-my.sharepoint.com/:b:/g/personal/sarun_monocept_com/EZTW1t60fJNOkUJvif-mT_cBQphWYUTmR-A82mFsqTrYcw?e=eJtuQi"));

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
