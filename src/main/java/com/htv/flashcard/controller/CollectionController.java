package com.htv.flashcard.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.htv.flashcard.DTO.FlashcardSetDTO;
import com.htv.flashcard.model.User;
import com.htv.flashcard.service.CollectionService;
import com.htv.flashcard.service.UserService;

@RestController
@RequestMapping("/api/collections")
public class CollectionController {
    @Autowired
    private CollectionService collectionService;


    @Autowired
    private UserService userService;

    /**
     * Lưu hoặc hủy lưu bộ flashcard vào collection
     */
    @PostMapping("/{setId}")
    public ResponseEntity<?> toggleCollection(@PathVariable Long setId, @AuthenticationPrincipal UserDetails ud) {
        User user = userService.findByEmail(ud.getUsername()).orElseThrow();
        collectionService.toggleCollection(user.getId(), setId);
        return ResponseEntity.ok("Cập nhật collection thành công");
    }

    /**
     * Lấy danh sách collection của user
     */
    @GetMapping
    public ResponseEntity<List<FlashcardSetDTO>> getCollections(@AuthenticationPrincipal UserDetails ud) {
        User u = userService.findByEmail(ud.getUsername()).orElseThrow();
        List<FlashcardSetDTO> dtos = u.getSavedFlashcardSets().stream()
            .map(fs -> new FlashcardSetDTO(
                fs.getId(),
                fs.getTitle(),
                fs.getDescription(),
                fs.getLastStudiedAt()!=null?fs.getLastStudiedAt():fs.getCreatedAt(),
                fs.getSavedByUsers().size(),         // số người đã lưu
                fs.getUser().getId(),
                fs.getUser().getFullName(),         // tên tác giả
                fs.getUser().getAvatarUrl(),
                fs.getFlashcards().size(),
                0.0
            ))
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
