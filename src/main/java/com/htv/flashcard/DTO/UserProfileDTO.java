package com.htv.flashcard.DTO;

import lombok.Data;
import java.util.List;

@Data
public class UserProfileDTO {
    private String fullName;
    private String email;
    private List<FlashcardSetDTO> flashcardSets;
}
