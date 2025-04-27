package com.htv.flashcard.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.htv.flashcard.DTO.QuizAttemptDTO;
import com.htv.flashcard.model.FlashcardSet;
import com.htv.flashcard.model.QuizAttempt;
import com.htv.flashcard.model.User;
import com.htv.flashcard.service.QuizAttemptService;
import com.htv.flashcard.service.UserService;

@RestController
@RequestMapping("/api/attempts")
public class QuizAttemptController {
    @Autowired private QuizAttemptService quizAttemptService;
    @Autowired private UserService userService;

    /**
     * Lưu thông tin lần làm quiz
     */
    @PostMapping
    public ResponseEntity<?> recordAttempt(@RequestBody QuizAttemptDTO dto, @AuthenticationPrincipal UserDetails ud){
        User user = userService.findByEmail(ud.getUsername()).orElseThrow();
        QuizAttempt attempt = new QuizAttempt();
        attempt.setUser(user);
        attempt.setFlashcardSet(new FlashcardSet(dto.getSetId()));
        attempt.setCorrectAnswers(dto.getCorrectAnswers());
        attempt.setTotalQuestions(dto.getTotalQuestions());
        attempt.setScore(dto.getScore());
        attempt.setDurationSeconds(dto.getDurationSeconds());
        return ResponseEntity.ok(quizAttemptService.recordAttempt(attempt));
    }

    /**
     * Lấy lịch sử quiz của user
     */
    @GetMapping("/user")
    public ResponseEntity<?> getUserAttempts(@AuthenticationPrincipal UserDetails ud) {
        User user = userService.findByEmail(ud.getUsername()).orElseThrow();
        return ResponseEntity.ok(quizAttemptService.getAttemptsByUser(user.getId()));
    }

    /**
     * Lấy lịch sử quiz theo bộ
     */
    @GetMapping("/set/{setId}")
    public ResponseEntity<List<QuizAttempt>> getSetAttempts(@PathVariable Long setId) {
        return ResponseEntity.ok(quizAttemptService.getAttemptsBySet(setId));
    }
}

