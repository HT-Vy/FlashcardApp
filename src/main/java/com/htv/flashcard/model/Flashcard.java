package com.htv.flashcard.model;


import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private FlashcardSet flashcardSet;
}