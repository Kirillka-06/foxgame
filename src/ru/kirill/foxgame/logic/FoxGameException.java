package ru.kirill.foxgame.logic;

import ru.kirill.foxgame.controller.GameController;

/**
 * Исключение для ошибок в игре "Лисица на опушке".
 * 
 * <p>Используется для индикации ошибок, связанных с игровой логикой,
 * таких как недопустимые ходы, нарушение правил или проблемы с состоянием игры.
 * 
 * @see FoxGame
 * @see GameController
 */
public class FoxGameException extends Exception {
    
    /**
     * Создает исключение с указанным сообщением об ошибке.
     * 
     * @param message сообщение об ошибке, описывающее причину исключения
     */
    public FoxGameException(String message) {
        super(message);
    }
    
    /**
     * Создает исключение с указанным сообщением об ошибке и причиной.
     * 
     * @param message сообщение об ошибке, описывающее причину исключения
     * @param cause исключение, которое стало причиной этого исключения
     */
    public FoxGameException(String message, Throwable cause) {
        super(message, cause);
    }
}
