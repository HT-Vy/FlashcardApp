package com.htv.flashcard.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.htv.flashcard.DTO.QuizAnswerInputDTO;
import com.htv.flashcard.DTO.QuizEvaluateDTO;
import com.htv.flashcard.model.Flashcard;
import com.htv.flashcard.service.QuizService;

@RestController
@RequestMapping("/api/sets/{setId}/quiz")
public class QuizController {

    @Autowired
    private QuizService quizService;

    /**
     * Lấy flashcard đã học (review mode)
     */
    @GetMapping("/review")
    public ResponseEntity<List<Flashcard>> review(@PathVariable Long setId) {
        return ResponseEntity.ok(quizService.getFlashcardsForQuiz(setId, true));
    }

    /**
     * Lấy flashcard chưa học (test mode)
     */
    @GetMapping("/test")
    public ResponseEntity<List<Flashcard>> test(@PathVariable Long setId) {
        return ResponseEntity.ok(quizService.getFlashcardsForQuiz(setId, false));
    }

    /**
     * Chấm câu trả lời ở review mode
     */
    @PostMapping("/review/evaluate")
    public ResponseEntity<QuizEvaluateDTO> evaluateReview(
            @PathVariable Long setId,
            @RequestBody QuizAnswerInputDTO input) {
        // Lấy flashcard cần chấm
        Flashcard fc = quizService.getFlashcardsForQuiz(setId, true)
                .stream()
                .filter(f -> f.getId().equals(input.getFlashcardId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Flashcard không tồn tại"));
        // Đánh giá và trả kết quả
        QuizEvaluateDTO result = quizService.evaluateFlashcard(fc, input.getUserAnswer(), true);
        return ResponseEntity.ok(result);
    }

    /**
     * Chấm câu trả lời ở test mode
     */
    @PostMapping("/test/evaluate")
    public ResponseEntity<QuizEvaluateDTO> evaluateTest(
            @PathVariable Long setId,
            @RequestBody QuizAnswerInputDTO input) {
        Flashcard fc = quizService.getFlashcardsForQuiz(setId, false)
                .stream()
                .filter(f -> f.getId().equals(input.getFlashcardId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Flashcard không tồn tại"));
        QuizEvaluateDTO result = quizService.evaluateFlashcard(fc, input.getUserAnswer(), false);
        return ResponseEntity.ok(result);
    }
}
