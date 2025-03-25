package com.monocept.chatbot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;

@Builder
@Entity
@Table(name = "option")
public class Option {

    @Id
    @SequenceGenerator(name =  "option_seq", sequenceName = "option_seq", initialValue = 1 , allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE , generator = "option_seq")
    private long id;

    public String name;

    @JsonIgnore
    public  boolean active;
}
