package edu.cit.lingguahey.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PotionUseResponse {
    private String message;
    private int updatedLives;
    private int updatedShield;
    private int updatedSkipsLeft;
    private boolean isLevelCleared;
}
