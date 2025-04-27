package com.htv.flashcard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.htv.flashcard.model.Status;
import com.htv.flashcard.service.FlashcardService;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {
    @Autowired
    private FlashcardService flashcardService;

    /**
     * Lấy flashcard theo trạng thái để làm quiz
     */
    @GetMapping("/{setId}")
    public ResponseEntity<?> getQuizFlashcards(@PathVariable Long setId, @RequestParam Status status) {
        return ResponseEntity.ok(flashcardService.getFlashcardsByStatus(setId, status));
    }

    /**
     * Gửi đáp án và cập nhật trạng thái flashcard
     */
    @PostMapping("/{flashcardId}/answer")
    public ResponseEntity<?> submitAnswer(@PathVariable Long flashcardId, @RequestParam boolean correct) {
        Status newStatus = correct ? Status.LEARNED : Status.UNLEARNED;
        return ResponseEntity.ok(flashcardService.updateFlashcardStatus(flashcardId, newStatus));
    }
}

