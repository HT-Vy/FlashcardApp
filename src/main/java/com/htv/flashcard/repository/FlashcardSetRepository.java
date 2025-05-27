package com.htv.flashcard.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.htv.flashcard.model.FlashcardSet;

@Repository
public interface FlashcardSetRepository extends JpaRepository<FlashcardSet, Long> {

    // tìm các set visible hoặc do chính chủ sở hữu
    @Query("""
      SELECT fs
        FROM FlashcardSet fs
       WHERE fs.visible = true
          OR fs.user.id = :ownerId
    """)
    List<FlashcardSet> findAllVisibleOrOwnedBy(@Param("ownerId") Long ownerId);

    // USER search: visible/owner + search keyword, sắp DESC
    @Query("""
      SELECT fs
        FROM FlashcardSet fs
   LEFT JOIN fs.ratings r
       WHERE (fs.visible = true OR fs.user.id = :ownerId)
         AND (LOWER(fs.title) LIKE %:kw% OR LOWER(fs.description) LIKE %:kw%)
    GROUP BY fs
    ORDER BY COALESCE(AVG(r.score), 0) DESC
    """)
    List<FlashcardSet> searchVisibleOrOwnedByOrderByAvgRatingDesc(
        @Param("ownerId") Long ownerId,
        @Param("kw")      String keyword
    );

  //   // USER: khi không search, sắp theo avg rating DESC
  //   @Query("""
  //     SELECT fs
  //       FROM FlashcardSet fs
  //  LEFT JOIN fs.ratings r
  //      WHERE fs.visible = true OR fs.user.id = :ownerId
  //   GROUP BY fs
  //   ORDER BY COALESCE(AVG(r.score), 0) DESC
  //   """)
  //   List<FlashcardSet> findAllVisibleOrOwnedByOrderByAvgRatingDesc(@Param("ownerId") Long ownerId);

    // ADMIN: lấy toàn bộ set, sắp theo avg rating ASC (thấp → cao)
    @Query("""
      SELECT fs
        FROM FlashcardSet fs
   LEFT JOIN fs.ratings r
    GROUP BY fs
    ORDER BY COALESCE(AVG(r.score), 0) ASC
    """)
    List<FlashcardSet> findAllOrderByAvgRatingAsc();

    // Top N bộ flashcard được lưu nhiều nhất
    @Query("""
      SELECT fs 
        FROM FlashcardSet fs 
   LEFT JOIN fs.savedByUsers u 
   WHERE fs.visible = true
    GROUP BY fs 
    ORDER BY COUNT(u) DESC
    """)
    List<FlashcardSet> findTopPopularSets(Pageable pageable);

    // tìm tất cả các set đang visible
    List<FlashcardSet> findAllByVisibleTrue();
}
