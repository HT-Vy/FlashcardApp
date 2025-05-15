package com.htv.flashcard.service;

import java.util.DoubleSummaryStatistics;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.htv.flashcard.model.*;
import com.htv.flashcard.repository.*;

@Service
public class RatingService {
    private final RatingRepository ratingRepo;
    private final FlashcardSetRepository setRepo;
    private final UserRepository userRepo;

    public RatingService(RatingRepository ratingRepo,
                         FlashcardSetRepository setRepo,
                         UserRepository userRepo) {
        this.ratingRepo = ratingRepo;
        this.setRepo    = setRepo;
        this.userRepo   = userRepo;
    }

    @Transactional
    public void rateSet(Long setId, Long userId, int score) {
        FlashcardSet set = setRepo.findById(setId)
            .orElseThrow(() -> new RuntimeException("Set không tồn tại"));
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        // 1. Không cho tác giả tự đánh giá
        if (set.getUser().getId().equals(userId)) {
            throw new RuntimeException("Tác giả không được phép đánh giá");
        }

        // 2. Lấy rating cũ (nếu có) để update hoặc tạo mới
        Rating rating = ratingRepo
            .findByUserAndFlashcardSet(user, set)
            .orElse(new Rating(null, user, set, score));

        rating.setScore(score);
        ratingRepo.save(rating);
    }

    @Transactional(readOnly = true)
    public double getAverageRating(Long setId) {
        List<Rating> list = ratingRepo.findByFlashcardSet(
            setRepo.getReferenceById(setId));
        if (list.isEmpty()) return 0;
        DoubleSummaryStatistics stats = list.stream()
            .mapToDouble(Rating::getScore)
            .summaryStatistics();
        return stats.getAverage();
    }
}
