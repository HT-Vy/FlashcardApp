package com.htv.flashcard.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.htv.flashcard.model.FlashcardSet;
import com.htv.flashcard.model.User;
import com.htv.flashcard.repository.FlashcardSetRepository;
import com.htv.flashcard.repository.UserRepository;

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
}
