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

import jakarta.persistence.EntityNotFoundException;
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
        // 1) Lấy entity FlashcardSet
        FlashcardSet set = flashcardSetRepo.findById(setId)
            .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bộ flashcard"));

        // 2) Xóa hết flashcard cũ(với orphanRemoval=true, Hibernate sẽ delete DB rows)
        set.getFlashcards().clear();
        
        // 3) Thêm lại tất cả flashcard mới
        List<Flashcard> newCards = dtos.stream().map(d -> {
            Flashcard f = new Flashcard();
            f.setFrontContent(d.getFrontContent());
            f.setBackContent(d.getBackContent());
            f.setFlashcardSet(set);
            return f;
        }).collect(Collectors.toList());
        set.getFlashcards().addAll(newCards);

        // 4) Lưu set (các flashcard sẽ được cascade)
        flashcardSetRepo.save(set);
    }

}
