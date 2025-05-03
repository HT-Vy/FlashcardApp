package com.htv.flashcard.DTO;

import java.util.List;

import lombok.Data;

@Data
public class FlashcardBatchDTO {
    private List<FlashcardDTO> flashcards;
    // getters/setters
  }
