package com.htv.flashcard.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.htv.flashcard.DTO.FlashcardSetDTO;
import com.htv.flashcard.model.FlashcardSet;
import com.htv.flashcard.model.Status;
import com.htv.flashcard.model.User;
import com.htv.flashcard.repository.FlashcardSetRepository;


@Service
public class DashboardService {

    @Autowired private FlashcardSetRepository setRepo;
    @Autowired private UserService userService;

    /**
     * Lấy danh sách bộ user tự tạo + đã lưu, sắp xếp theo thời gian học gần nhất,
     * nếu chưa học thì theo thời gian tạo
     */
    public List<FlashcardSetDTO> getRecentStudySets(Long userId, int limit) {
        User user = userService.getUserById(userId);

        // Union các bộ do user tạo và đã lưu
        Set<FlashcardSet> allSets = new HashSet<>(user.getFlashcardSets());
        allSets.addAll(user.getSavedFlashcardSets());

        // Sort giảm dần theo lastStudiedAt (nếu null dùng createdAt)
        return allSets.stream()
            .sorted(Comparator.<FlashcardSet, LocalDateTime>comparing(
                fs -> fs.getLastStudiedAt() != null ? fs.getLastStudiedAt() : fs.getCreatedAt()
            ).reversed())
            .limit(limit)
            .map(fs -> {
                int total = fs.getFlashcards().size();
                long learned = fs.getFlashcards().stream()
                                 .filter(f -> f.getStatus() == Status.LEARNED)
                                 .count();
                double percent = total > 0 ? (learned * 100.0 / total) : 0.0;
                return new FlashcardSetDTO(
                    fs.getId(),
                    fs.getTitle(),
                    fs.getDescription(),
                    fs.getLastStudiedAt() != null ? fs.getLastStudiedAt() : fs.getCreatedAt(),
                    fs.getSavedByUsers().size(),
                    fs.getUser().getId(),
                    fs.getUser().getFullName(),
                    fs.getUser().getAvatarUrl(),
                    total,
                    percent
                );
            })
            .collect(Collectors.toList());
    }

    /** Top popular: dùng query ở repository */
    public List<FlashcardSetDTO> getTopPopularSets(int limit) {
        return setRepo.findTopPopularSets(PageRequest.of(0, limit))
            .stream()
            .map(fs -> {
                int total = fs.getFlashcards().size();
                long learned = fs.getFlashcards().stream()
                                 .filter(f -> f.getStatus() == Status.LEARNED)
                                 .count();
                double percent = total > 0 ? (learned * 100.0 / total) : 0.0;
                return new FlashcardSetDTO(
                    fs.getId(),
                    fs.getTitle(),
                    fs.getDescription(),
                    fs.getLastStudiedAt() != null ? fs.getLastStudiedAt() : fs.getCreatedAt(),
                    fs.getSavedByUsers().size(),
                    fs.getUser().getId(),
                    fs.getUser().getFullName(),
                    fs.getUser().getAvatarUrl(),
                    total,
                    percent
                );
            })
            .collect(Collectors.toList());
    }
}

