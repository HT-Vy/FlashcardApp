package com.htv.flashcard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.htv.flashcard.service.RatingService;
import com.htv.flashcard.service.UserService;
import com.htv.flashcard.model.User;

@RestController
@RequestMapping("/api/sets/{setId}/rating")
public class RatingController {
    private final RatingService ratingService;
    @Autowired
    private UserService userService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    // 1) Đánh giá hoặc cập nhật đánh giá
    @PostMapping
    public ResponseEntity<?> rate(
            @PathVariable Long setId,
            @RequestParam int score,
            @AuthenticationPrincipal UserDetails ud) {
        try {
            User u = userService.findByEmail(ud.getUsername()).orElseThrow(()-> new RuntimeException("Không tìm thấy người dùng"));
            ratingService.rateSet(setId, u.getId(), score);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            // Ví dụ bạn chuyển throw RuntimeException thành IllegalArgumentException
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    // 2) Lấy điểm trung bình
    @GetMapping("/average")
    public ResponseEntity<Double> average(@PathVariable Long setId) {
        return ResponseEntity.ok(ratingService.getAverageRating(setId));
    }
}
