package com.monocept.chatbot.service.impl;

import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOServer;
import com.monocept.chatbot.component.ChatEventHandler;
import com.monocept.chatbot.component.SocketClientManager;
import com.monocept.chatbot.config.SocketIOConfig;
import com.monocept.chatbot.entity.History;
import com.monocept.chatbot.entity.Message;
import com.monocept.chatbot.enums.MessageSendType;
import com.monocept.chatbot.enums.MessageStatus;
import com.monocept.chatbot.exceptions.MessageNotFoundException;
import com.monocept.chatbot.exceptions.ResourcesNotFoundException;
import com.monocept.chatbot.exceptions.SessionNotFoundException;
import com.monocept.chatbot.model.dto.HistoryDTO;
import com.monocept.chatbot.model.dto.MessageDTO;
import com.monocept.chatbot.model.dto.ReceiveMessageDTO;
import com.monocept.chatbot.model.request.SendMessageRequest;
import com.monocept.chatbot.model.response.MLIMessageResponse;
import com.monocept.chatbot.model.response.MasterResponse;
import com.monocept.chatbot.model.response.ReceiveMessageResponse;
import com.monocept.chatbot.model.response.SendMessageResponse;
import com.monocept.chatbot.reposiotry.MessageRepository;
import com.monocept.chatbot.reposiotry.RedisChatHistoryRepository;
import com.monocept.chatbot.service.MessageService;
import io.netty.channel.unix.Socket;
import io.socket.client.IO;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import com.corundumstudio.socketio.SocketIOClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class MessageServiceImpl implements MessageService {

    @Value("${socketio.client.url}")
    private String socketIoClientUrl;

    @Value("${mli.message.url}")
    private String messageUrl;

    private final MessageRepository messageRepository;
    private final ModelMapper modelMapper;
    private WebClient.Builder webClientBuilder;
    private final RedisChatHistoryRepository redisChatHistoryRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private SocketIOClient client;
    private final SocketIOServer server;
    private SocketClientManager clientManager;

    public MessageServiceImpl(MessageRepository messageRepository, ModelMapper modelMapper, RedisChatHistoryRepository redisChatHistoryRepository,
                              RedisTemplate<String, String> redisTemplate, SocketIOServer server, SocketClientManager clientManager) {
        this.messageRepository = messageRepository;
        this.modelMapper = modelMapper;
        this.redisChatHistoryRepository = redisChatHistoryRepository;
        this.redisTemplate = redisTemplate;
        this.server = server;
        this.clientManager = clientManager;
    }

    @Override
    @Transactional
    public SendMessageResponse processMessage(SendMessageRequest messageRequest) {
        Message message = getMessage(messageRequest);
        String session = getSession(message.getUserId());// move before database call
        List<MessageDTO> chatHistoryDetailsEmail = redisChatHistoryRepository.getChatHistoryDetailsEmail(messageRequest.getEmailId());
        chatHistoryDetailsEmail.add(modelMapper.map(message, MessageDTO.class));
        redisChatHistoryRepository.saveChatHistoryDetails(messageRequest.getEmailId(), chatHistoryDetailsEmail);

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


        return sendMessageResponse;
    }

    @Async
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
        Message message = new Message();
        message.setUserId(messageDTO.getUserId());
        message.setEmail(messageDTO.getEmailId());
        message.setSendType(messageDTO.getSendType());
        message.setMessageType(messageDTO.getMessageType());
        message.setMessageId(UUID.randomUUID().toString());
        message.setMessageTo(messageDTO.getMessageTo());
        message.setText(messageDTO.getText());
        message.setReplyToMessageId(messageDTO.getReplyToMessageId());
        message.setStatus(MessageStatus.PENDING);
        message.setEmoji(messageDTO.getEmoji());
        message.setAction(messageDTO.getAction());
//        message.setOptions();
//        message.setBotOptions();
        message.setPlatform(messageDTO.getPlatform());
        message.setCreatedAt(ZonedDateTime.now());
        message.setUpdatedAt(ZonedDateTime.now());
        return message;
    }

    private String getSession(String userId) {
        String sessionId = (String) redisTemplate.opsForHash().get("session:user", userId);
        if(sessionId == null) {
            throw new SessionNotFoundException(userId);
        } else{
           client = clientManager.getClient(userId);
        }
        sessionId = (String) redisTemplate.opsForHash().get("session:user", userId);
        return sessionId;
    }

    private Mono<MasterResponse<MLIMessageResponse>> sendMessageToMLI(MessageDTO messageDTO) {
        WebClient webClient = webClientBuilder.build();

        return webClient.post()
                .uri(messageUrl)
                .body(Mono.just(messageDTO), MessageDTO.class)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<MasterResponse<MLIMessageResponse>>() {})
                .doOnError(error -> System.err.println("Error sending to third party: " + error.getMessage()));
    }

    @Override
    public ReceiveMessageResponse receiveMessage(ReceiveMessageDTO receiveMessageDTO) {
        Message message = mapBotMessage( receiveMessageDTO);
        messageRepository.save(message);
        String sessionId = getSession(message.getUserId());
//        getSession(receiveMessageDTO.getUserId());
        List<MessageDTO> chatHistoryDetailsEmail = redisChatHistoryRepository.getChatHistoryDetailsEmail(receiveMessageDTO.getEmailId());
        chatHistoryDetailsEmail.add(modelMapper.map(message, MessageDTO.class));
        redisChatHistoryRepository.saveChatHistoryDetails(receiveMessageDTO.getEmailId(), chatHistoryDetailsEmail);
        client = clientManager.getClient(receiveMessageDTO.getUserId());
//        client.sendEvent("chat_message", "botResponse");
        client.sendEvent("message", receiveMessageDTO.getEntry().getMessage().getText());
        client.sendEvent("chat_message", receiveMessageDTO.getEntry().getMessage().getText());
        long epochSeconds = message.getCreatedAt().toEpochSecond();
        return ReceiveMessageResponse.builder().messageId(message.getMessageId()).timestamp(epochSeconds).build();
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
//        message.setOptions(receiveMessageDTO.getEntry().getMessage().getOptions());
        message.setPlatform(receiveMessageDTO.getPlatform());
        message.setCreatedAt(ZonedDateTime.now(ZoneOffset.UTC));

        return message;
    }

    public com.corundumstudio.socketio.SocketIOClient getClientBySessionId(String sessionId) {
        for (com.corundumstudio.socketio.SocketIOClient client : server.getAllClients()) {
            if (client.getSessionId().toString().equals(sessionId)) {
                return client;
            }
        }
        return null; // or throw exception if not found
    }

}
