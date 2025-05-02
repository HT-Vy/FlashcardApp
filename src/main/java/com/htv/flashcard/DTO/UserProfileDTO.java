package com.htv.flashcard.DTO;

import lombok.Data;
import java.util.List;

@Data
public class UserProfileDTO {
    private Long id; 
    private String fullName;
    private String email;
    private String avatarUrl;
    private List<FlashcardSetDTO> flashcardSets;
}
