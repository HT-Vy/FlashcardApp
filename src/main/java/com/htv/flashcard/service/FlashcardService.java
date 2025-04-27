package com.htv.flashcard.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.htv.flashcard.DTO.FlashcardDTO;
import com.htv.flashcard.model.Flashcard;
import com.htv.flashcard.model.FlashcardSet;
import com.htv.flashcard.model.Status;
import com.htv.flashcard.repository.FlashcardRepository;
import com.htv.flashcard.repository.FlashcardSetRepository;

@Service
public class FlashcardService {
    @Autowired
    private FlashcardRepository flashcardRepository;
    @Autowired
    private FlashcardSetRepository flashcardSetRepository;

    public Flashcard addFlashcard(Long setId, Flashcard flashcard) {
        FlashcardSet set = flashcardSetRepository.findById(setId).orElseThrow();
        flashcard.setFlashcardSet(set);
        return flashcardRepository.save(flashcard);
    }

    public Flashcard updateFlashcard(Long id, FlashcardDTO dto) {
        Flashcard f = flashcardRepository.findById(id).orElseThrow();
        f.setFrontContent(dto.getFrontContent());
        f.setBackContent(dto.getBackContent());
        return flashcardRepository.save(f);
    }

    public void deleteFlashcard(Long id) {
        flashcardRepository.deleteById(id);
    }

    public Flashcard updateFlashcardStatus(Long flashcardId, Status status) {
        Flashcard flashcard = flashcardRepository.findById(flashcardId).orElseThrow();
        flashcard.setStatus(status);
        return flashcardRepository.save(flashcard);
    }

    public List<Flashcard> getFlashcardsByStatus(Long setId, Status status) {
        return flashcardRepository.findByFlashcardSetIdAndStatus(setId, status);
    }
}
