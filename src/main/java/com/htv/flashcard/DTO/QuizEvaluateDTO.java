package com.htv.flashcard.DTO;

import lombok.Data;

@Data
public class QuizEvaluateDTO {
    private Long flashcardId;
    private boolean correct;
    // getters & setters
}
