package com.htv.flashcard.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class FlashcardSet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime lastStudiedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")   
    @JsonBackReference 
    private User user;

    @OneToMany(mappedBy = "flashcardSet", cascade = CascadeType.ALL)
    private List<Flashcard> flashcards = new ArrayList<>();
    
    
    @ManyToMany(mappedBy = "savedFlashcardSets")
    @JsonIgnore
    private List<User> savedByUsers = new ArrayList<>();

    public FlashcardSet() {}
    public FlashcardSet(Long id) { this.id = id; }
}
