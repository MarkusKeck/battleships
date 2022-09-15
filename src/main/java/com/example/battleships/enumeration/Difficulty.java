package com.example.battleships.enumeration;

public enum Difficulty {

    VERY_EASY(0),
    EASY(20),
    MEDIUM(40),
    HARD(60),
    VERY_HARD(80),
    UNBEATABLE(100);

    public final Integer accuracy;

    Difficulty(Integer accuracy) {
        this.accuracy = accuracy;
    }

}
