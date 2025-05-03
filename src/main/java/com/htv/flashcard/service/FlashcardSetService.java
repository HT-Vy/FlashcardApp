package com.htv.flashcard.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.htv.flashcard.DTO.FlashcardDTO;
import com.htv.flashcard.DTO.FlashcardSetDTO;
import com.htv.flashcard.model.Flashcard;
import com.htv.flashcard.model.FlashcardSet;
import com.htv.flashcard.model.User;
import com.htv.flashcard.repository.FlashcardRepository;
import com.htv.flashcard.repository.FlashcardSetRepository;

import jakarta.transaction.Transactional;

@Service
public class FlashcardSetService {
    @Autowired
    private FlashcardSetRepository flashcardSetRepo;
    @Autowired 
    private FlashcardRepository flashcardRepo;

    public FlashcardSet createSet(FlashcardSet set, User user) {
        set.setUser(user);
        return flashcardSetRepo.save(set);
    }

    public List<FlashcardSet> searchSets(String keyword) {
        return flashcardSetRepo.findByTitleContainingOrDescriptionContaining(keyword, keyword);
    }

    public FlashcardSet getSetById(Long id) {
        return flashcardSetRepo.findById(id).orElseThrow();
    }
    public FlashcardSet updateSet(Long id, FlashcardSetDTO dto) {
        FlashcardSet set = flashcardSetRepo.findById(id).orElseThrow();
        set.setTitle(dto.getTitle());
        set.setDescription(dto.getDescription());
        return flashcardSetRepo.save(set);
    }

    public void deleteSet(Long id) {
        flashcardSetRepo.deleteById(id);
    }

    @Transactional
    public void addFlashcardsBatch(Long setId, List<FlashcardDTO> dtos) {
        FlashcardSet set = flashcardSetRepo.findById(setId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy set"));
        List<Flashcard> cards = dtos.stream().map(dto -> {
        Flashcard f = new Flashcard();
        f.setFrontContent(dto.getFrontContent());
        f.setBackContent(dto.getBackContent());
        f.setFlashcardSet(set);
        return f;
        }).collect(Collectors.toList());
        flashcardRepo.saveAll(cards);
    }
}
