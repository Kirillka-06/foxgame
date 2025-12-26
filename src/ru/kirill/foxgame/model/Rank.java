package ru.kirill.foxgame.model;

/**
 * Represents the rank (value) of a card.
 */
public enum Rank {
    ONE(1, "1"),
    TWO(2, "2"),
    THREE(3, "3"),
    FOUR(4, "4"),
    FIVE(5, "5"),
    SIX(6, "6"),
    SEVEN(7, "7"),
    EIGHT(8, "8"),
    NINE(9, "9"),
    TEN(10, "10"),
    ELEVEN(11, "11");
    
    private final int value;
    private final String symbol;
    
    Rank(int value, String symbol) {
        this.value = value;
        this.symbol = symbol;
    }
    
    public int getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return symbol;
    }
}
