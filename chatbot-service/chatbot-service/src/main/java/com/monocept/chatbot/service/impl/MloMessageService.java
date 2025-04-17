//package com.monocept.chatbot.service.impl;
//
//import com.monocept.chatbot.service.SendMessageService;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.HashMap;
//import java.util.Map;
//
//
//@Service
//public class MloMessageService implements SendMessageService {
//
//
//    @Value("${mli.message.send.url}")
//    private String apiUrl;
//
//    private final RestTemplate restTemplate;
//
//    public MloMessageService(RestTemplate restTemplate) {
//        this.restTemplate = restTemplate;
//    }
//
//    @Override
//    public void send(String message) {
//
//        // Create headers
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        // Create request body
//        Map<String, String> requestBody = new HashMap<>();
//        requestBody.put("message", message);
//
//        // Create HttpEntity with headers and body
//        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);
//
//        // Make the POST request
//        String response = restTemplate.postForObject(apiUrl, requestEntity, String.class);
//    }
//}
