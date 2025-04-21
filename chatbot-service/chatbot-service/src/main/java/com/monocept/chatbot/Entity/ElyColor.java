package com.monocept.chatbot.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "elycolor")
public class ElyColor {

    @Id
    @SequenceGenerator(name = "elycolor_seq", sequenceName = "elycolor_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "elycolor_seq")
    private Long id;

    @Column(length = 50)
    private String themeName;          // "dark", "light", or custom name

    @Column(length = 50)
    private String backgroundColor;    // Chat area background

    @Column(length = 100)
    private String userMessageColor;   // User message background

    @Column(length = 100)
    private String botMessageColor;    // Bot message background

    @Column(length = 20)
    private String borderColor;        // Chat bubble border

    @Column(length = 20)
    private String buttonColor;        // Send button background

    @Column(length = 20)
    private String coachOptionColor;

    @Column(length = 20)
    private String botOptionColor;

    private ZonedDateTime createdAt;

    private ZonedDateTime updatedAt;

    @Column(name = "is_active")
    private boolean isActive;


}
