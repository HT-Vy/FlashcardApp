package com.htv.flashcard.DTO;

import lombok.Data;

@Data
public class QuizAttemptDTO {
    private Long setId;
    private Integer correctAnswers;
    private Integer totalQuestions;
    private Float score;
    private Integer durationSeconds;
}

