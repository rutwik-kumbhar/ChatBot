package com.monocept.chatbot.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class MediaDto {

    private List<MediaDetail> video;
    private List<MediaDetail> image;
    private List<MediaDetail> document;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MediaDetail {
        private String format;
        private List<String> mediaUrl;
    }

}
