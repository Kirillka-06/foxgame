package ru.kirill.foxgame.model;


import java.util.Objects;


/**
 * Представляет игральную карту в игре.
 * Каждая карта имеет масть ({@link Suit}) и достоинство ({@link Rank}).
 * Карты с нечетными достоинствами (1, 3, 5, 7, 9, 11) обладают особыми эффектами.
 */
public class Card {
    private final Suit suit;
    private final Rank rank;
    
    /**
     * Создает новую карту.
     *
     * @param suit масть карты
     * @param rank достоинство карты
     */
    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    /**
     * Возвращает масть карты.
     *
     * @return масть карты
     */
    public Suit getSuit() { 
        return suit; 
    }

    /**
     * Возвращает достоинство карты.
     *
     * @return достоинство карты
     */
    public Rank getRank() { 
        return rank; 
    }

    /**
     * Возвращает числовое значение карты.
     *
     * @return значение от 1 до 11
     */
    public int getValue() { 
        return rank.getValue(); 
    }

    /**
     * Проверяет, является ли карта нечетной (имеет особый эффект).
     *
     * @return true, если значение карты нечетное
     */
    public boolean isOdd() { 
        return getValue() % 2 == 1; 
    }

    /**
     * Проверяет, является ли карта козырем для указанной козырной масти.
     *
     * @param trumpSuit козырная масть для проверки
     * @return true, если карта является козырем
     */
    public boolean isTrump(Suit trumpSuit) {
        return this.suit == trumpSuit;
    }
    
    /**
     * Возвращает текстовое описание объекта.
     * 
     * @return строковое описание карты
     */
    @Override
    public String toString() {
        return rank + " " + suit;
    }
    
    /**
     * Проверяет равенство свойств обЪекта объекта.
     * 
     * @return boolean-значение
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return suit == card.suit && rank == card.rank;
    }
    
    /**
     * Возвращает хэш объекта.
     * 
     * @return int
     */
    @Override
    public int hashCode() {
        return Objects.hash(suit, rank);
    }
}