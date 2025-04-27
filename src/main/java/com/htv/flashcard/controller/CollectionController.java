package com.htv.flashcard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<?> getCollections(@AuthenticationPrincipal UserDetails ud) {
        User user = userService.findByEmail(ud.getUsername()).orElseThrow();
        return ResponseEntity.ok(collectionService.getUserCollections(user.getId()));
    }
}
