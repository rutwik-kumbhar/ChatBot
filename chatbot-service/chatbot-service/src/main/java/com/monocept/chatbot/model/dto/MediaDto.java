package com.monocept.chatbot.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class MediaDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<MediaDetail> video;
    private List<MediaDetail> image;
    private List<MediaDetail> document;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MediaDetail  implements Serializable{
        private static final long serialVersionUID = 1L;
        private String format;
        private List<String> mediaUrl;
    }

}
