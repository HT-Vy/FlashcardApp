package com.htv.flashcard.DTO;

import java.util.List;

import lombok.Data;

@Data
public class FlashcardSetDetailDTO {
    private Long id;
    private String title;
    private String description;
    private List<FlashcardDTO> flashcards;
    private boolean collected;
    private boolean ownedByCurrentUser;
}
