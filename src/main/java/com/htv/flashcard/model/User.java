package com.htv.flashcard.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String email;
    private String password;
    private boolean enabled = true;

    @OneToMany(mappedBy = "user")
    private List<FlashcardSet> flashcardSets = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "user_collections",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "flashcard_set_id")
    )
    private List<FlashcardSet> savedFlashcardSets = new ArrayList<>();
}