package ru.kirill.foxgame.model;

import java.util.Objects;

/**
 * Represents a card in the game.
 */
public class Card {
    private final Suit suit;
    private final Rank rank;
    
    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }
    
    public Suit getSuit() { 
        return suit; 
    }
    
    public Rank getRank() { 
        return rank; 
    }
    
    public int getValue() { 
        return rank.getValue(); 
    }
    
    public boolean isOdd() { 
        return getValue() % 2 == 1; 
    }
    
    public boolean isTrump(Suit trumpSuit) {
        return this.suit == trumpSuit;
    }
    
    @Override
    public String toString() {
        return rank + " " + suit;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return suit == card.suit && rank == card.rank;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(suit, rank);
    }
}