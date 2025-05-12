package com.monocept.chatbot.entity;


import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "placeholder")
public class PlaceHolder {

    @Id
    @SequenceGenerator(name =  "placeholder_seq", sequenceName = "placeholder_seq", initialValue = 1 , allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE , generator = "placeholder_seq")
    @Column(name = "id")
    private long id;


    @Column( unique = true , length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String icon;

}
