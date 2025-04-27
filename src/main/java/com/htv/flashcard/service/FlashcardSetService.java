package com.htv.flashcard.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.htv.flashcard.DTO.FlashcardSetDTO;
import com.htv.flashcard.model.FlashcardSet;
import com.htv.flashcard.model.User;
import com.htv.flashcard.repository.FlashcardSetRepository;

@Service
public class FlashcardSetService {
    @Autowired
    private FlashcardSetRepository flashcardSetRepository;

    public FlashcardSet createSet(FlashcardSet set, User user) {
        set.setUser(user);
        return flashcardSetRepository.save(set);
    }

    public List<FlashcardSet> searchSets(String keyword) {
        return flashcardSetRepository.findByTitleContainingOrDescriptionContaining(keyword, keyword);
    }

    public FlashcardSet getSetById(Long id) {
        return flashcardSetRepository.findById(id).orElseThrow();
    }
    public FlashcardSet updateSet(Long id, FlashcardSetDTO dto) {
        FlashcardSet set = flashcardSetRepository.findById(id).orElseThrow();
        set.setTitle(dto.getTitle());
        set.setDescription(dto.getDescription());
        return flashcardSetRepository.save(set);
    }

    public void deleteSet(Long id) {
        flashcardSetRepository.deleteById(id);
    }
}
