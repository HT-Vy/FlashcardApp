package com.htv.flashcard.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data 
@AllArgsConstructor
public class QuizCardDTO {
    private Long id;       // flashcard.getId()
    private String backContent;     // flashcard.getBackContent()
    private String frontContent;     // flashcard.getFrontContent()
}
