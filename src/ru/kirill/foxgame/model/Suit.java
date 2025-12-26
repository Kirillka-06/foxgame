package ru.kirill.foxgame.model;

/**
 * Represents the suit of a card.
 */
public enum Suit {
    KEY("Ключ"),
    BELL("Колокольчик"),
    MOON("Луна");
    
    private final String displayName;
    
    Suit(String displayName) {
        this.displayName = displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}