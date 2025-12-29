package ru.kirill.foxgame.model;


import ru.kirill.foxgame.view.CardComponent;


/**
 * Представляет позицию на игровом поле.
 * Используется для определения расположения карт на столе или в интерфейсе.
 * 
 * <p>Координаты задаются в пикселях относительно левого верхнего угла компонента.
 * 
 * @see CardComponent
 */
public class Position {
    private final int x;
    private final int y;
    
    /**
     * Создает позицию с указанными координатами.
     * 
     * @param x координата X (горизонтальная)
     * @param y координата Y (вертикальная)
     */
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Возвращает координату X позиции.
     * 
     * @return координата X
     */
    public int getX() { 
        return x; 
    }
    
    /**
     * Возвращает координату Y позиции.
     * 
     * @return координата Y
     */
    public int getY() { 
        return y; 
    }
    
    /**
     * Возвращает строковое представление позиции в формате "(X, Y)".
     * 
     * @return строковое представление позиции
     */
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
