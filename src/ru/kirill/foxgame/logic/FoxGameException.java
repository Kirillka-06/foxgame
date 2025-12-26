package ru.kirill.foxgame.logic;

/**
 * Исключение для ошибок в игре.
 */
public class FoxGameException extends Exception {
    
    public FoxGameException(String message) {
        super(message);
    }
    
    public FoxGameException(String message, Throwable cause) {
        super(message, cause);
    }
}
