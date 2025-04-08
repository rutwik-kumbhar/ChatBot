package com.monocept.chatbot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
@EntityScan(basePackages = "com.monocept.chatbot.Entity")
public class ChatbotServiceApplication {
	public static void main(String[] args) {

		SpringApplication.run(ChatbotServiceApplication.class, args);

	}


}

