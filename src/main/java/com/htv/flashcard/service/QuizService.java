package com.htv.flashcard.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.htv.flashcard.DTO.QuizCardDTO;
import com.htv.flashcard.DTO.QuizEvaluateDTO;
import com.htv.flashcard.model.Flashcard;
import com.htv.flashcard.model.FlashcardSet;
import com.htv.flashcard.model.Status;
import com.htv.flashcard.repository.FlashcardRepository;
import com.htv.flashcard.repository.FlashcardSetRepository;

@Service
public class QuizService {
    @Autowired private FlashcardRepository flashcardRepo;
    @Autowired private FlashcardSetRepository flashcardSetRepo;
    

    /**
     * Lấy danh sách flashcard cho quiz.
     * @param setId    ID bộ flashcard
     * @param isReview true = review (chỉ thẻ LEARNED), false = test (chỉ thẻ UNLEARNED)
     * @return Danh sách flashcard tương ứng
     */
    public List<QuizCardDTO> getQuizCards(Long setId, boolean isReview) {
        Status want = isReview ? Status.LEARNED : Status.UNLEARNED;
        return flashcardRepo.findByFlashcardSetIdAndStatus(setId, want)
            .stream()
            .map(f -> new QuizCardDTO(f.getId(), f.getBackContent(), f.getFrontContent()))
            .collect(Collectors.toList());
    }

/**
     * Đánh giá 1 flashcard và trả về DTO kết quả.
     * @param fc         Flashcard lấy từ DB
     * @param userAnswer Đáp án người dùng nhập
     * @param isReview   true nếu review, false nếu test
     * @return QuizEvaluateDTO chứa flashcardId và kết quả đúng/sai
     */
    // Đánh giá 1 flashcard và cập nhật status ngay
    public QuizEvaluateDTO evaluateFlashcard(Flashcard fc, String userAnswer, boolean isReview) {
        boolean isCorrect = fc.getFrontContent().trim().equalsIgnoreCase(userAnswer.trim());
        if (isReview) {
            if (!isCorrect) fc.setStatus(Status.UNLEARNED);
        } else {
            if (isCorrect) fc.setStatus(Status.LEARNED);
        }
        flashcardRepo.save(fc);

        // Cập nhật lastStudiedAt bộ flashcard set
        FlashcardSet set = fc.getFlashcardSet();
        set.setLastStudiedAt(LocalDateTime.now());
        flashcardSetRepo.save(set);

        QuizEvaluateDTO dto = new QuizEvaluateDTO();
        dto.setFlashcardId(fc.getId());
        dto.setCorrect(isCorrect);
        return dto;
    }
    /**
     * Tính % tiến độ học: số thẻ LEARNED trên tổng số thẻ
     */
    public double calculateProgress(Long setId) {
        // Lấy tất cả flashcard của bộ
        List<Flashcard> all = flashcardRepo.findByFlashcardSetId(setId);
        if (all.isEmpty()) {
            return 0.0; // Tránh chia 0
        }
        // Đếm thẻ đã học
        long learned = all.stream()
                .filter(f -> f.getStatus() == Status.LEARNED)
                .count();
        // Tính % và trả về
        return (100.0 * learned) / all.size();
    }
}
