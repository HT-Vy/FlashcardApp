package com.htv.flashcard.DTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlashcardSetDetailDTO {
    private Long id;
    private String title;
    private String description;
    private String ownerEmail;           // ai tạo bộ này
    private List<FlashcardDTO> flashcards;
    private boolean collected;
    private boolean ownedByCurrentUser;
    private boolean visible;
    private Double averageRating;        // điểm trung bình (1–5)
}
