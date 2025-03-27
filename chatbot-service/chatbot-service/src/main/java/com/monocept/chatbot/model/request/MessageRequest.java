package com.monocept.chatbot.model.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequest {
  //  @Email(message = "Invalid email format")
    public String email;
    public int page;
    public int size;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
