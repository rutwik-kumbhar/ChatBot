package com.monocept.chatbot.entity;


import com.monocept.chatbot.enums.StatusFlag;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;


@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @SequenceGenerator(name =  "user_seq", sequenceName = "user_seq", initialValue = 1 , allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE , generator = "user_seq")
    @Column(name = "id")
    private long id;

    @Column(length = 10)
    private String ssoId;

    @Column(length = 10)
    private String agentId;

    @Column(length =  100, nullable = false)
    private String firstName;

    private String lastName;

    @Column(length = 30 , nullable = false, unique = true )
    private String emailId;

    @Column(length = 20, nullable = false)
    private String role;

    @Column(length = 100)
    private String firebaseId;

    @Column(length = 100 , nullable = false)
    private String deviceId;

    @Column(length = 100)
    private String sessionId;

    @Enumerated(EnumType.STRING)
    private StatusFlag statusFlag;

//    @CreationTimestamp // Automatically set on insert time
    private ZonedDateTime createdAt;

//    @UpdateTimestamp  // Automatically set on insert time
    private ZonedDateTime updatedAt;

}
