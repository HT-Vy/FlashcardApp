package com.htv.flashcard.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.htv.flashcard.DTO.QuizEvaluateDTO;
import com.htv.flashcard.model.Flashcard;
import com.htv.flashcard.model.Status;
import com.htv.flashcard.repository.FlashcardRepository;

@Service
public class QuizService {
    @Autowired private FlashcardRepository flashcardRepo;
    

    /**
     * Lấy danh sách flashcard cho quiz.
     * @param setId    ID bộ flashcard
     * @param isReview true = review (chỉ thẻ LEARNED), false = test (chỉ thẻ UNLEARNED)
     * @return Danh sách flashcard tương ứng
     */
    public List<Flashcard> getFlashcardsForQuiz(Long setId, boolean isReview) {
        Status status = isReview ? Status.LEARNED : Status.UNLEARNED;
        return flashcardRepo.findByFlashcardSetIdAndStatus(setId, status);
    }
/**
     * Đánh giá 1 flashcard và trả về DTO kết quả.
     * @param fc         Flashcard lấy từ DB
     * @param userAnswer Đáp án người dùng nhập
     * @param isReview   true nếu review, false nếu test
     * @return QuizEvaluateDTO chứa flashcardId và kết quả đúng/sai
     */
    public QuizEvaluateDTO evaluateFlashcard(Flashcard fc, String userAnswer, boolean isReview) {
        String correctAnswer = fc.getBackContent().trim().toLowerCase();
        String input        = userAnswer.trim().toLowerCase();
        boolean isCorrect   = correctAnswer.equals(input);

        // Cập nhật trạng thái thẻ
        if (isReview) {
            if (!isCorrect) {
                fc.setStatus(Status.UNLEARNED);
            }
        } else {
            if (isCorrect) {
                fc.setStatus(Status.LEARNED);
            }
        }
        flashcardRepo.save(fc);

        // Tạo và trả về DTO kết quả
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
