package com.monocept.chatbot;

import com.monocept.chatbot.Entity.History;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDateTime;

@SpringBootApplication
@EnableScheduling
public class ChatbotServiceApplication {
	public static void main(String[] args) {

		SpringApplication.run(ChatbotServiceApplication.class, args);

	}


}

