package com.htv.flashcard.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.htv.flashcard.model.FlashcardSet;

@Repository
public interface FlashcardSetRepository extends JpaRepository<FlashcardSet, Long> {
    List<FlashcardSet> findByTitleContainingOrDescriptionContaining(String title, String description);
    /**
     * Lấy Top N bộ flashcard được lưu nhiều nhất.
     * LEFT JOIN để vẫn lấy cả bộ chưa ai lưu (COUNT = 0), nhưng ORDER BY DESC nên sẽ xếp cuối.
     */
    @Query("""
      SELECT fs 
      FROM FlashcardSet fs 
      LEFT JOIN fs.savedByUsers u 
      GROUP BY fs 
      ORDER BY COUNT(u) DESC
    """)
    List<FlashcardSet> findTopPopularSets(Pageable pageable);
}
