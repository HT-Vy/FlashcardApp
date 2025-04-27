package com.htv.flashcard.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.htv.flashcard.model.QuizAttempt;
import com.htv.flashcard.repository.QuizAttemptRepository;

@Service
public class QuizAttemptService {
    @Autowired 
    private QuizAttemptRepository quizAttemptRepository;

    public QuizAttempt recordAttempt(QuizAttempt attempt) {
        return quizAttemptRepository.save(attempt);
    }

    public List<QuizAttempt> getAttemptsByUser(Long userId) {
        return quizAttemptRepository.findByUserId(userId);
    }

    public List<QuizAttempt> getAttemptsBySet(Long setId) {
        return quizAttemptRepository.findByFlashcardSetId(setId);
    }
}

