package com.htv.flashcard.model;


import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Flashcard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String frontContent;
    private String backContent;

    @Enumerated(EnumType.STRING)
    private Status status = Status.UNLEARNED;

    @ManyToOne
    @JoinColumn(name = "flashcard_set_id")
    @JsonBackReference
    private FlashcardSet flashcardSet;
}