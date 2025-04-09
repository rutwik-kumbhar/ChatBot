package com.monocept.chatbot;
import jakarta.annotation.PostConstruct;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@SpringBootApplication
@EnableScheduling
@EntityScan(basePackages = "com.monocept.chatbot.Entity")
public class ChatbotServiceApplication {

	private static final Logger log = LoggerFactory.getLogger(ChatbotServiceApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ChatbotServiceApplication.class, args);

	}


}

