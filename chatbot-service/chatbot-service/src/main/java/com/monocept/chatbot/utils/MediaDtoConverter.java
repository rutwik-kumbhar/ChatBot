package com.monocept.chatbot.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monocept.chatbot.model.dto.MediaDto;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Configuration
//@Converter(autoApply = false)
@Slf4j
public class MediaDtoConverter {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String convertToDatabaseColumn(MediaDto mediaDto) {
        log.info("convertToDatabaseColumn: Converting media dto to " + mediaDto);
        if (mediaDto == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(mediaDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting MediaDto to JSON", e);
        }
    }

    public static MediaDto convertToEntityAttribute(String dbData) {
        log.info("convertToEntityAttribute: Converting media dto to " + dbData);
        if (dbData == null || dbData.isBlank() || dbData.equals("null")) {
            return null;
        }
        try {
            return objectMapper.readValue(dbData, MediaDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting JSON to MediaDto", e);
        }
    }



}
