package edu.cit.lingguahey.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GuessResponse {
    private boolean isCorrect;
    private String feedback;
    private int lives;
    private boolean isGameOver;
    private String correctAnswer;
}
