package ru.kirill.foxgame.model;


/**
 * Представляет игрока в игре.
 * Игра рассчитана на двух игроков: человека (PLAYER_1) и компьютера (PLAYER_2).
 */
public enum Player {
    /**
     * Первый игрок.
     */
    PLAYER_1("Игрок 1"),

    /**
     * Второй игрок.
     */
    PLAYER_2("Игрок 2");
    
    private final String displayName;
    

    Player(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * Возвращает текстовое описание объекта.
     * 
     * @return строковое описание игрока
     */
    @Override
    public String toString() {
        return displayName;
    }
    
    /**
     * Возвращает противника текущего игрока.
     * 
     * @return противник текущего игрока
     * @throws IllegalStateException если игрок не распознан (невозможно в нормальных условиях)
     */
    public Player opponent() {
        return this == PLAYER_1 ? PLAYER_2 : PLAYER_1;
    }
}
