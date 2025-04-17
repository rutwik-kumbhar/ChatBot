package com.monocept.chatbot.entity;


import com.monocept.chatbot.enums.BotCommunicationFlow;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Builder
@Data
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @SequenceGenerator(name =  "user_seq", sequenceName = "user_seq", initialValue = 1 , allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE , generator = "user_seq")
    @Column(name = "id")
    private long id;

    @Column(length = 10)
    private String agentId;

    @Column(length =  50, nullable = false)
    private String firstName;

    @Column(length =  50, nullable = false)
    private String lastName;

    @Column(length = 30 , nullable = false, unique = true )
    private String email;

    @Column(length = 20, nullable = false)
    private String role;

    @Column(length = 100)
    private String firebaseId;

    @Column(length = 100 , nullable = false)
    private String deviceId;

    @Enumerated(EnumType.STRING)
    private BotCommunicationFlow statusFlag;

    private ZonedDateTime createdAt;

    private ZonedDateTime updatedAt;

}
