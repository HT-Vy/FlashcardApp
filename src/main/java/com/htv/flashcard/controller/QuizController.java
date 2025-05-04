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
import org.springframework.web.server.ResponseStatusException;

import com.htv.flashcard.DTO.QuizAnswerInputDTO;
import com.htv.flashcard.DTO.QuizCardDTO;
import com.htv.flashcard.DTO.QuizEvaluateDTO;
import com.htv.flashcard.model.Flashcard;
import com.htv.flashcard.model.FlashcardSet;
import com.htv.flashcard.model.User;
import com.htv.flashcard.repository.FlashcardRepository;
import com.htv.flashcard.service.FlashcardSetService;
import com.htv.flashcard.service.QuizService;
import com.htv.flashcard.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/sets/{setId}/quiz")
public class QuizController {

    @Autowired
    private QuizService quizService;
    @Autowired
    private FlashcardSetService setService;
    @Autowired
    private FlashcardRepository flashcardRepo;

    @Autowired
    private UserService userService;

    private void checkAccess(Long userId, Long setId) {
        FlashcardSet set = setService.getSetById(setId);
        User user = userService.getUserById(userId);
        if (!set.getUser().getId().equals(userId)
         && !user.getSavedFlashcardSets().contains(set)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    // GET /review hoặc /test
    @GetMapping({"/review","/test"})
    public List<QuizCardDTO> quiz(
        @PathVariable Long setId,
        @AuthenticationPrincipal UserDetails ud,
        HttpServletRequest req) {

      Long userId = userService.findByEmail(ud.getUsername()).get().getId();
      checkAccess(userId, setId);
      boolean isReview = req.getRequestURI().endsWith("review");
      return quizService.getQuizCards(setId, isReview);
    }

    // POST /review/evaluate hoặc /test/evaluate
    @PostMapping({"/review/evaluate","/test/evaluate"})
    public QuizEvaluateDTO eval(
        @PathVariable Long setId,
        @RequestBody QuizAnswerInputDTO in,
        @AuthenticationPrincipal UserDetails ud,
        HttpServletRequest req) {

      Long userId = userService.findByEmail(ud.getUsername()).get().getId();
      checkAccess(userId, setId);
      boolean isReview = req.getRequestURI().contains("review");

      // Lấy thẳng flashcard từ repo để đảm bảo status mới nhất
      Flashcard fc = flashcardRepo.findById(in.getFlashcardId())
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

      return quizService.evaluateFlashcard(fc, in.getUserAnswer(), isReview);
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
        // 2. Kiểm quyền — ném 403 nếu không đủ quyền
        checkAccess(current.getId(), setId);
        double percent = quizService.calculateProgress(setId);
        return ResponseEntity.ok(percent);
    }
}
