package com.htv.flashcard.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

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
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    private String avatarUrl = "/assets/img/avatar.png";
     @Enumerated(EnumType.STRING)
    // @Column(nullable = false)
    private Role role = Role.USER;   // mặc định USER


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<FlashcardSet> flashcardSets = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "user_collections",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "flashcard_set_id")
    )
    @JsonIgnore 
    private List<FlashcardSet> savedFlashcardSets = new ArrayList<>();
}