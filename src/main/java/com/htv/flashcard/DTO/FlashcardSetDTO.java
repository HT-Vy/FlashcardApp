package com.htv.flashcard.DTO;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlashcardSetDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime lastStudiedAt;
    private int savedByCount;
    private Long ownerId;
    private String ownerName;
    private String ownerAvatarUrl;

    private int flashcardCount;
    private double progressPercent;
    private double averageRating;
}

