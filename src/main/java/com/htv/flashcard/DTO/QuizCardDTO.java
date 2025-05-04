package com.htv.flashcard.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data 
@AllArgsConstructor
public class QuizCardDTO {
    private Long id;       // flashcard.getId()
    private String vi;     // flashcard.getBackContent()
    private String en;     // flashcard.getFrontContent()
}
