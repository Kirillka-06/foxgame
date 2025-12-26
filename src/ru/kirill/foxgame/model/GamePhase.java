package ru.kirill.foxgame.model;

/**
 * Represents the current phase of the game.
 */
public enum GamePhase {
    DEALING("Раздача карт"),
    TRUMP_SELECTION("Выбор козыря"),
    PLAYING("Игра"),
    TRICK_COMPLETED("Круг завершен"),
    ROUND_SCORING("Подсчет очков за кон"),
    GAME_OVER("Игра завершена");
    
    private final String description;
    
    GamePhase(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return description;
    }
}
