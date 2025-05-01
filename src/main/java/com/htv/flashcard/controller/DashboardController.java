package com.htv.flashcard.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.htv.flashcard.DTO.FlashcardSetDTO;
import com.htv.flashcard.service.DashboardService;
import com.htv.flashcard.service.UserService;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired private DashboardService dashService;
    @Autowired private UserService userService;

    /** 1. Greeting */
    @GetMapping("/me")
    public ResponseEntity<String> greeting(@AuthenticationPrincipal UserDetails ud) {
        String fullName = userService.findByEmail(ud.getUsername()).orElseThrow().getFullName();
        return ResponseEntity.ok(fullName);
    }

    /** 2. Recent-study */
    @GetMapping("/recent")
    public ResponseEntity<List<FlashcardSetDTO>> recent(
            @AuthenticationPrincipal UserDetails ud) {
        Long userId = userService.findByEmail(ud.getUsername()).orElseThrow().getId();
        return ResponseEntity.ok(dashService.getRecentStudySets(userId, 10));
    }

    /** 3. Top popular */
    @GetMapping("/popular")
    public ResponseEntity<List<FlashcardSetDTO>> popular() {
        return ResponseEntity.ok(dashService.getTopPopularSets(10));
    }
}
