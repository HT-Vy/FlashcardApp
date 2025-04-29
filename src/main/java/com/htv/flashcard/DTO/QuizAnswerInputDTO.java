package com.htv.flashcard.DTO;

import lombok.Data;

@Data
public class QuizAnswerInputDTO {
    private Long flashcardId;
    private String userAnswer;
}
