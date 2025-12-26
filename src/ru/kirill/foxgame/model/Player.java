package ru.kirill.foxgame.model;

/**
 * Represents a player in the game.
 */
public enum Player {
    PLAYER_1("Игрок 1"),
    PLAYER_2("Игрок 2");
    
    private final String displayName;
    
    Player(String displayName) {
        this.displayName = displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
    
    /**
     * Returns the opponent of this player.
     */
    public Player opponent() {
        return this == PLAYER_1 ? PLAYER_2 : PLAYER_1;
    }
}
