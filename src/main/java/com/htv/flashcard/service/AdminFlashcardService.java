package com.htv.flashcard.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.htv.flashcard.DTO.FlashcardDTO;
import com.htv.flashcard.DTO.FlashcardSetDetailDTO;
import com.htv.flashcard.model.FlashcardSet;
import com.htv.flashcard.repository.FlashcardRepository;
import com.htv.flashcard.repository.FlashcardSetRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class AdminFlashcardService {

    @Autowired private FlashcardSetRepository setRepo;
    @Autowired private FlashcardRepository cardRepo;

    /**
     * Lấy chi tiết bộ flashcard, map vào DTO chứa:
     * id, title, description, ownerEmail, visible,
     * flashcards (front/back), collected, ownedByCurrentUser, averageRating
     */
    @Transactional(readOnly = true)
    public FlashcardSetDetailDTO getSetDetail(Long setId) {
        // 1. Lấy entity
        FlashcardSet set = setRepo.findById(setId)
            .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy set " + setId));

        // 2. Tính averageRating
        double avg = set.getRatings().stream()
            .mapToInt(r -> r.getScore())
            .average()
            .orElse(0.0);

        // 3. Map flashcards thành danh sách DTO (chỉ front/back)
        List<FlashcardDTO> cards = cardRepo.findByFlashcardSetId(setId).stream()
            .map(c -> new FlashcardDTO(
                c.getFrontContent(),
                c.getBackContent()
            ))
            .collect(Collectors.toList());

    
        // 5. Build DTO
        FlashcardSetDetailDTO dto = new FlashcardSetDetailDTO();
        dto.setId(set.getId());
        dto.setTitle(set.getTitle());
        dto.setDescription(set.getDescription());
        dto.setOwnerEmail(set.getUser().getEmail());
        dto.setVisible(set.getVisible());
        dto.setFlashcards(cards);
        dto.setAverageRating(avg);
        return dto;
    }
}