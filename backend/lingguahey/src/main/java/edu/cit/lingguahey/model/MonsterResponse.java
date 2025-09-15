package edu.cit.lingguahey.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonsterResponse {
    private int monsterId;
    private String tagalogName;
    private String englishName;
    private String description;
    private byte[] imageData;
    private List<Character> jumbledLetters;
}
