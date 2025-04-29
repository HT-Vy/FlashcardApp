package com.htv.flashcard.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.htv.flashcard.DTO.QuizAnswerInputDTO;
import com.htv.flashcard.DTO.QuizEvaluateDTO;
import com.htv.flashcard.model.Flashcard;
import com.htv.flashcard.model.FlashcardSet;
import com.htv.flashcard.model.User;
import com.htv.flashcard.service.FlashcardSetService;
import com.htv.flashcard.service.QuizService;
import com.htv.flashcard.service.UserService;

@RestController
@RequestMapping("/api/sets/{setId}/quiz")
public class QuizController {

    @Autowired
    private QuizService quizService;
    @Autowired
    private FlashcardSetService setService;

    @Autowired
    private UserService userService;

    /**
     * Kiểm tra quyền truy cập: user phải là owner hoặc đã lưu bộ flashcard
     */
    private boolean hasAccess(Long userId, Long setId) {
        // Lấy FlashcardSet
        FlashcardSet set = setService.getSetById(setId);
        // Lấy User
        User user = userService.getUserById(userId);
        // Quản trị viên logic: user là creator hoặc đã lưu trong collection
        return set.getUser().getId().equals(userId)
                || user.getSavedFlashcardSets().contains(set);
    }

    /**
     * Ôn tập (review): chỉ cho phép flashcard đã học. Yêu cầu xác thực.
     */
    @GetMapping("/review")
    public ResponseEntity<?> review(
            @PathVariable Long setId,
            @AuthenticationPrincipal UserDetails ud) {
        // Lấy user hiện tại
        User current = userService.findByEmail(ud.getUsername())
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
        // 403 nếu không có quyền
        if (!hasAccess(current.getId(), setId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Bạn không có quyền truy cập ôn tập bộ này");
        }
        // Trả về danh sách flashcard đã học
        List<Flashcard> list = quizService.getFlashcardsForQuiz(setId, true);
        return ResponseEntity.ok(list);
    }
    /**
     * Kiểm tra (test): chỉ cho phép flashcard chưa học. Yêu cầu xác thực.
     */
    @GetMapping("/test")
    public ResponseEntity<?> test(
            @PathVariable Long setId,
            @AuthenticationPrincipal UserDetails ud) {
        User current = userService.findByEmail(ud.getUsername())
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
        if (!hasAccess(current.getId(), setId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Bạn không có quyền truy cập kiểm tra bộ này");
        }
        List<Flashcard> list = quizService.getFlashcardsForQuiz(setId, false);
        return ResponseEntity.ok(list);
    }

     /**
     * Đánh giá flashcard cho review mode. Yêu cầu xác thực.
     */
    @PostMapping("/review/evaluate")
    public ResponseEntity<?> evaluateReview(
            @PathVariable Long setId,
            @RequestBody QuizAnswerInputDTO input,
            @AuthenticationPrincipal UserDetails ud) {
        User current = userService.findByEmail(ud.getUsername())
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
        if (!hasAccess(current.getId(), setId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Bạn không có quyền đánh giá ôn tập bộ này");
        }
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
    * Đánh giá flashcard cho test mode. Yêu cầu xác thực.
    */
   @PostMapping("/test/evaluate")
   public ResponseEntity<?> evaluateTest(
           @PathVariable Long setId,
           @RequestBody QuizAnswerInputDTO input,
           @AuthenticationPrincipal UserDetails ud) {
       User current = userService.findByEmail(ud.getUsername())
               .orElseThrow(() -> new RuntimeException("User không tồn tại"));
       if (!hasAccess(current.getId(), setId)) {
           return ResponseEntity.status(HttpStatus.FORBIDDEN)
                   .body("Bạn không có quyền đánh giá kiểm tra bộ này");
       }
        Flashcard fc = quizService.getFlashcardsForQuiz(setId, false)
                .stream()
                .filter(f -> f.getId().equals(input.getFlashcardId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Flashcard không tồn tại"));
        QuizEvaluateDTO result = quizService.evaluateFlashcard(fc, input.getUserAnswer(), false);
        return ResponseEntity.ok(result);
    }
    /**
     * Lấy tiến độ học của bộ: % flashcard đã learned. Yêu cầu xác thực.
     */
    @GetMapping("/progress")
    public ResponseEntity<?> getProgress(
            @PathVariable Long setId,
            @AuthenticationPrincipal UserDetails ud) {
        User current = userService.findByEmail(ud.getUsername())
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
        if (!hasAccess(current.getId(), setId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Bạn không có quyền xem tiến độ bộ này");
        }
        double percent = quizService.calculateProgress(setId);
        return ResponseEntity.ok(percent);
    }
}
