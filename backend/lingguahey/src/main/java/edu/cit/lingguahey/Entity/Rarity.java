package edu.cit.lingguahey.Entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Rarity {
    COMMON(74.5),    // 74.5% chance
    RARE(20),       // 20% chance
    LEGENDARY(5),   // 5% chance
    MYTHIC(0.5);    // 0.5% chance

    @Getter
    private final double pullChance;

}
