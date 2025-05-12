package com.monocept.chatbot.service.impl;

import com.corundumstudio.socketio.SocketIOClient;
import com.monocept.chatbot.component.SocketIOClientProvider;
import com.monocept.chatbot.entity.Message;
import com.monocept.chatbot.enums.MessageSendType;
import com.monocept.chatbot.enums.MessageStatus;
import com.monocept.chatbot.exceptions.MessageNotFoundException;
import com.monocept.chatbot.model.dto.MessageDto;
import com.monocept.chatbot.model.dto.ReceiveMessageDTO;
import com.monocept.chatbot.model.request.MessageCountRequest;
import com.monocept.chatbot.model.request.ReceiveMessageRequest;
import com.monocept.chatbot.model.request.SendMessageRequest;
import com.monocept.chatbot.model.response.MLIMessageResponse;
import com.monocept.chatbot.model.response.MasterResponse;
import com.monocept.chatbot.model.response.ReceiveMessageResponse;
import com.monocept.chatbot.model.response.SendMessageResponse;
import com.monocept.chatbot.reposiotry.MessageRepository;
import com.monocept.chatbot.service.MessageService;
import com.monocept.chatbot.utils.BotUtility;
import com.monocept.chatbot.utils.MediaDtoConverter;
import com.monocept.chatbot.utils.RedisUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {

    @Value("${mli.message.url}")
    private String messageUrl;

    private final MessageRepository messageRepository;
    private final ModelMapper modelMapper;
    private WebClient.Builder webClientBuilder;
    private final RedisTemplate<String, String> redisTemplate;
    private final SocketIOClientProvider clientManager;
    private final RedisUtility redisUtility;
    private final BotUtility botUtility;

    @Override
    public void processMessage(SendMessageRequest messageRequest) {
        log.info("Process Message: {}", messageRequest);
        Message message = getMessage(messageRequest);
        redisUtility.saveLiveMessageToRedisSortedSet(messageRequest.getUserId(), message);

        SendMessageResponse sendMessageResponse = new SendMessageResponse();
//        sendMessageToMLI(messageDTO)
//                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
//                .subscribe(response -> {
//            if (response.getData().getAcknowledgement().toString().equalsIgnoreCase(MessageStatus.DELIVERED.toString())) {
//                System.out.println("Message successfully delivered.");
//                sendMessageResponse.setMessageId(messageDTO.getMessageId());
//                sendMessageResponse.setAcknowledgement(response.getData().getAcknowledgement());
//                message.setStatus(response.getData().getAcknowledgement());
//                // repo call
//            } else {
//                //other scenario, if any
//            }
//        }, error -> System.err.println("Error in third-party call: " + error.getMessage()));

        sendMessageResponse.setMessageId(message.getMessageId());
        sendMessageResponse.setAcknowledgement(MessageStatus.DELIVERED);

        SocketIOClient client= clientManager.getClientByUserId(messageRequest.getUserId());
        log.info("processMessage : client : {} ", client);
        MasterResponse<SendMessageResponse> response = new MasterResponse<>("success", HttpStatus.OK.value(),"Messages sent successfully.", sendMessageResponse);
        client.sendEvent("acknowledgement", response);

        ReceiveMessageRequest receiveMessageRequest = botUtility.getBotResponse(messageRequest);
        log.info("processMessage : receiveMessageRequest {}" , receiveMessageRequest);
        try {
            ReceiveMessageDTO requestDto = modelMapper.map(receiveMessageRequest, ReceiveMessageDTO.class);
            log.info("processMessage : requestDto : {}" , requestDto);
            receiveMessage(requestDto);
        }catch (Exception exception){
            log.info("processMessage : Error {}", exception.getMessage());
        }

    }


    public Message getMessage(SendMessageRequest messageDTO) {
        Message message;
        if(messageDTO.getSendType().equals(MessageSendType.REACTION)){
             message = messageRepository.findByMessageId(messageDTO.getMessageId())
                    .orElseThrow(() -> new MessageNotFoundException("Message not found!"));
             mapUserReaction(messageDTO, message);
        } else if (messageDTO.getSendType().equals(MessageSendType.ACKNOWLEDGEMENT)) {
            message = messageRepository.findByMessageId(messageDTO.getMessageId())
                    .orElseThrow(() -> new MessageNotFoundException("Message not found!"));
            message.setStatus(messageDTO.getStatus());
        } else{
            message = mapUserMessage(messageDTO);
        }
        messageRepository.save(message);
        return message;
    }

    private void mapUserReaction(SendMessageRequest messageDTO, Message message) {
        message.setAction(messageDTO.getAction());
        message.setEmoji(messageDTO.getEmoji());
        message.setUpdatedAt(ZonedDateTime.now(ZoneOffset.UTC));
    }

    private Message mapUserMessage(SendMessageRequest messageDTO) {
        return Message.builder()
                .userId(messageDTO.getUserId())
                .email(messageDTO.getEmailId())
                .sendType(messageDTO.getSendType())
                .messageType(messageDTO.getMessageType())
                .messageId(messageDTO.getMessageId())
                .messageTo(messageDTO.getMessageTo())
                .text(messageDTO.getText())
                .replyToMessageId(messageDTO.getReplyToMessageId())
                .status(MessageStatus.PENDING)
                .emoji(messageDTO.getEmoji())
                .action(messageDTO.getAction())
                .platform(messageDTO.getPlatform())
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();
    }

    private Mono<MasterResponse<MLIMessageResponse>> sendMessageToMLI(MessageDto messageDTO) {
        WebClient webClient = webClientBuilder.build();

        return webClient.post()
                .uri(messageUrl)
                .body(Mono.just(messageDTO), MessageDto.class)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<MasterResponse<MLIMessageResponse>>() {})
                .doOnError(error -> System.err.println("Error sending to third party: " + error.getMessage()));
    }

    @Override
    public ReceiveMessageResponse receiveMessage(ReceiveMessageDTO receiveMessageDTO) {
        Message message = mapBotMessage( receiveMessageDTO);
        messageRepository.save(message);
        log.info("Message received: {}", message);
        receiveMessageDTO.setMessageId(message.getMessageId());
        log.info("receiveMessage : date {} " , message.getCreatedAt());

        receiveMessageDTO.setCreatedAt(message.getCreatedAt().toString());
//        redisUtility.saveMessageToRedisSortedSet(receiveMessageDTO.getUserId(),message);
        SocketIOClient client= clientManager.getClientByUserId(receiveMessageDTO.getUserId());
        client.sendEvent("bot_message", receiveMessageDTO);
        long epochSeconds = message.getCreatedAt().toEpochSecond();
        return ReceiveMessageResponse.builder().messageId(message.getMessageId()).timestamp(epochSeconds).build();
    }

    @Override
    public Map<String, Long> getMessageCountSendByBot(MessageCountRequest request) {
        long count = messageRepository.countDeliveredBotMessagesByAgentId(request.getUserId());
       return   Collections.singletonMap("count", count);
    }

    private Message mapBotMessage(ReceiveMessageDTO receiveMessageDTO) {
        Message message = new Message();
        String messageId = UUID.randomUUID().toString();
        message.setUserId(receiveMessageDTO.getUserId());
        message.setEmail(receiveMessageDTO.getEmailId());
        message.setMessageId(messageId);
        message.setMessageTo(receiveMessageDTO.getEntry().getMessageTo());
        message.setText(receiveMessageDTO.getEntry().getMessage().getText());
        message.setReplyToMessageId(receiveMessageDTO.getEntry().getReplyToMessageId());
        message.setStatus(MessageStatus.DELIVERED);
//        message.setEmoji(); not needed
//        message.setAction();  not needed
        message.setBotOptions(receiveMessageDTO.getEntry().getMessage().isBotOption());
        message.setOptions(receiveMessageDTO.getEntry().getMessage().getOptions());
        message.setPlatform(receiveMessageDTO.getPlatform());
        message.setCreatedAt(ZonedDateTime.now());
        message.setMedia(MediaDtoConverter.convertToDatabaseColumn(receiveMessageDTO.getEntry().getMessage().getMedia()));
        return message;
    }

}
