package com.htv.flashcard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.htv.flashcard.model.Flashcard;
import com.htv.flashcard.model.Status;

@Repository
public interface FlashcardRepository extends JpaRepository<Flashcard, Long> {
    List<Flashcard> findByFlashcardSetIdAndFrontContentContaining(Long setId, String keyword);
    List<Flashcard> findByFlashcardSetIdAndStatus(Long setId, Status status);
    //List<Flashcard> findByFlashcardSet_SetIdAndLearned(Long setId, boolean learned);
}
