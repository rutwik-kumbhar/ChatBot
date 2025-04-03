package com.monocept.chatbot.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monocept.chatbot.model.dto.MediaDto;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.context.annotation.Configuration;

@Configuration
@Converter(autoApply = true)
public class MediaDtoConverter implements AttributeConverter<MediaDto, String> {

    private final ObjectMapper objectMapper;

    public MediaDtoConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    @Override
    public String convertToDatabaseColumn(MediaDto mediaDto) {
        try {
            return objectMapper.writeValueAsString(mediaDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting MediaDto to JSON", e);
        }
    }

    @Override
    public MediaDto convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, MediaDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting JSON to MediaDto", e);
        }
    }

}
