package com.htv.flashcard.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class QuizAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private FlashcardSet flashcardSet;

    private Integer correctAnswers;
    private Integer totalQuestions;
    private Float score;
    private LocalDateTime attemptTime = LocalDateTime.now();
    private Integer durationSeconds;
}
