package com.monocept.chatbot.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@Entity
@Table(name = "option")
public class Option {

    @Id
    @SequenceGenerator(name =  "option_seq", sequenceName = "option_seq", initialValue = 1 , allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE , generator = "option_seq")
    private long id;

    private String name;
    private String icon;
}
