// package com.htv.flashcard.repository;

// import java.util.List;

// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.stereotype.Repository;

// import com.htv.flashcard.model.QuizAttempt;

// @Repository
// public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
//     List<QuizAttempt> findByUserId(Long userId);
//     List<QuizAttempt> findByFlashcardSetId(Long setId);
// }