package com.htv.flashcard.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.htv.flashcard.model.FlashcardSet;
import com.htv.flashcard.model.User;
import com.htv.flashcard.repository.FlashcardSetRepository;
import com.htv.flashcard.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CollectionService {
    @Autowired 
    private UserRepository userRepository;
    @Autowired 
    private FlashcardSetRepository flashcardSetRepository;

    public void toggleCollection(Long userId, Long setId) {
        User user = userRepository.findById(userId).orElseThrow();
        FlashcardSet set = flashcardSetRepository.findById(setId).orElseThrow();
        if (user.getSavedFlashcardSets().contains(set)) {
            user.getSavedFlashcardSets().remove(set);
        } else {
            user.getSavedFlashcardSets().add(set);
        }
        userRepository.save(user);
    }

    public List<FlashcardSet> getUserCollections(Long userId) {
        return userRepository.findById(userId).orElseThrow().getSavedFlashcardSets();
    }
    /**
     * Trả về true nếu user đã lưu set này (dựa trên getUserCollections).
     */
    public boolean isCollected(Long userId, Long setId) {
        // 1. Lấy danh sách đã lưu, trả về List<FlashcardSet> user lưu.
        List<FlashcardSet> saved = getUserCollections(userId);
        // 2. Kiểm tra bằng id
        return saved.stream()
                    .anyMatch(set -> set.getId().equals(setId));
    }
}
