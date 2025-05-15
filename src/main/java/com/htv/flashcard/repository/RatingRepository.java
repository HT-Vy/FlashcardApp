package com.htv.flashcard.repository;

import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import com.htv.flashcard.model.*;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByUserAndFlashcardSet(User user, FlashcardSet flashcardSet);
    List<Rating> findByFlashcardSet(FlashcardSet flashcardSet);
}
