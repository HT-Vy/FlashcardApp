// package com.htv.flashcard.model;

// import java.time.LocalDateTime;

// import com.fasterxml.jackson.annotation.JsonBackReference;

// import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.JoinColumn;
// import jakarta.persistence.ManyToOne;
// import lombok.Data;

// @Entity
// @Data
// public class QuizAttempt {
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     @ManyToOne
//     @JoinColumn(name = "user_id")
//     @JsonBackReference  // không serialize ngược về User
//     private User user;

//     @ManyToOne
//     @JoinColumn(name = "flashcard_set_id")
//     @JsonBackReference  // không serialize ngược về FlashcardSet
//     private FlashcardSet flashcardSet;

//     private Integer correctAnswers;
//     private Integer totalQuestions;
//     private Float score;
//     private LocalDateTime attemptTime = LocalDateTime.now();
//     private Integer durationSeconds;
// }
