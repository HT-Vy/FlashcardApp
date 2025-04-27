package com.htv.flashcard.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.htv.flashcard.DTO.FlashcardDTO;
import com.htv.flashcard.model.Flashcard;
import com.htv.flashcard.model.Status;
import com.htv.flashcard.service.FlashcardService;

@RestController
@RequestMapping("/api/flashcards")
public class FlashcardController {
    @Autowired
    private FlashcardService flashcardService;

    /**
     * Thêm flashcard vào bộ
     */
    @PostMapping("/{setId}")
    public ResponseEntity<?> addFlashcard(@PathVariable Long setId, @RequestBody FlashcardDTO dto) {
        Flashcard flashcard = new Flashcard();
        flashcard.setFrontContent(dto.getFrontContent());
        flashcard.setBackContent(dto.getBackContent());
        return ResponseEntity.ok(flashcardService.addFlashcard(setId, flashcard));
    }

    /**
     * Cập nhật flashcard
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateFlashcard(@PathVariable Long id, @RequestBody FlashcardDTO dto) {
        return ResponseEntity.ok(flashcardService.updateFlashcard(id, dto));
    }

    /**
     * Xóa flashcard
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFlashcard(@PathVariable Long id) {
        flashcardService.deleteFlashcard(id);
        return ResponseEntity.ok("Xóa flashcard thành công");
    }

    /**
     * Tìm flashcard trong bộ theo từ khóa
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchFlashcards(@RequestParam Long setId, @RequestParam String keyword) {
        List<Flashcard> list = flashcardService.getFlashcardsByStatus(setId, Status.UNLEARNED)
            .stream().filter(f -> f.getFrontContent().contains(keyword)).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }


    @GetMapping("/set/{setId}")
    public ResponseEntity<List<FlashcardDTO>> getBySet(@PathVariable Long setId) {
        List<FlashcardDTO> list = flashcardService.getFlashcardsByStatus(setId, Status.UNLEARNED)
            .stream().map(f -> { FlashcardDTO d = new FlashcardDTO(); d.setFrontContent(f.getFrontContent()); d.setBackContent(f.getBackContent()); return d; })
            .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

}

