package com.htv.flashcard.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.htv.flashcard.DTO.AdminSetSummaryDTO;
import com.htv.flashcard.DTO.FlashcardSetDetailDTO;
import com.htv.flashcard.service.AdminFlashcardService;
import com.htv.flashcard.service.FlashcardSetService;

@RestController
@RequestMapping("/api/admin/flashcardsets")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final FlashcardSetService setService;
    private final AdminFlashcardService adminService;

    public AdminController(FlashcardSetService setService,
                           AdminFlashcardService adminService) {
        this.setService = setService;
        this.adminService = adminService;
    }
    /** Danh sách mọi set (admin) */
    @GetMapping
    public List<AdminSetSummaryDTO> allSets() {
        return setService.listAllByRatingAsc().stream()
            .map(fs -> {
                double avg = fs.getRatings().stream()
                                .mapToInt(r -> r.getScore())
                                .average().orElse(0.0);
                return new AdminSetSummaryDTO(
                    fs.getId(),
                    fs.getTitle(),
                    fs.getSavedByUsers().size(),
                    fs.getFlashcards().size(),
                    fs.getUser().getFullName(),
                    fs.getVisible(),
                    avg
                );
                })
                .collect(Collectors.toList());
    }

    /** Ẩn set */
    @PutMapping("/{id}/hide")
    public ResponseEntity<?> hide(@PathVariable Long id) {
        setService.updateVisibility(id, false);
        return ResponseEntity.ok().build();
    }

    /** Hiện set */
    @PutMapping("/{id}/show")
    public ResponseEntity<?> show(@PathVariable Long id) {
        setService.updateVisibility(id, true);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public FlashcardSetDetailDTO detail(@PathVariable Long id) {
        return adminService.getSetDetail(id);
    }
}
