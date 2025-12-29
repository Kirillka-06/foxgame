package ru.kirill.foxgame.model;


/**
 * Представляет масть карты в игре "Лисица на опушке".
 * Игра использует три уникальные масти: Ключ, Колокольчик и Луна.
 * 
 * <p>Масть определяет принадлежность карты к одной из трех групп и влияет
 * на правила игры, особенно при определении козырной масти.
 * 
 * @see Card
 * @see Rank
 */
public enum Suit {
    /**
     * Масть "Ключ".
     */
    KEY("Ключ"),

    /**
     * Масть "Колокольчик".
     */
    BELL("Колокольчик"),

    /**
     * Масть "Луна".
     */
    MOON("Луна");

    private final String displayName;

    /**
     * Создает масть с русским названием.
     *
     * @param displayName отображаемое имя масти
     */
    Suit(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Возвращает русское название масти.
     *
     * @return строковое представление масти
     */
    @Override
    public String toString() {
        return displayName;
    }
}