package com.htv.flashcard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.htv.flashcard.model.FlashcardSet;

@Repository
public interface FlashcardSetRepository extends JpaRepository<FlashcardSet, Long> {
    List<FlashcardSet> findByTitleContainingOrDescriptionContaining(String title, String description);
}
