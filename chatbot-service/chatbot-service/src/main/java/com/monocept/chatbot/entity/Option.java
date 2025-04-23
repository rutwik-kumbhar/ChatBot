package com.monocept.chatbot.entity;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "option")
public class Option {

    @Id
    @SequenceGenerator(name =  "option_seq", sequenceName = "option_seq", initialValue = 1 , allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE , generator = "option_seq")
    private long id;
    
    @Column(unique = true , length = 200 , nullable = false)
    private String name;

    @Column(length = 255)
    private String icon;
}
