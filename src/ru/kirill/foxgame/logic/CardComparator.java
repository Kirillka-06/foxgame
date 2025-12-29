package ru.kirill.foxgame.logic;


import ru.kirill.foxgame.model.*;
import java.util.Comparator;


/**
 * Компаратор для сортировки карт по масти и достоинству.
 * 
 * <p>Карты сортируются в следующем порядке:
 * <ol>
 *   <li>Козырные карты (если указана козырная масть)</li>
 *   <li>Карты по масти в порядке определения в перечислении {@link Suit}</li>
 *   <li>Карты одной масти сортируются по возрастанию достоинства</li>
 * </ol>
 * 
 * @see Card
 * @see Suit
 */
public class CardComparator implements Comparator<Card> {
    private final Suit trumpSuit;
    
    /**
     * Создает компаратор для сортировки карт с учетом козырной масти.
     * 
     * @param trumpSuit козырная масть для учета при сортировке, может быть {@code null}
     */
    public CardComparator(Suit trumpSuit) {
        this.trumpSuit = trumpSuit;
    }
    
    /**
     * Сравнивает две карты для определения их порядка.
     * 
     * @param card1 первая карта для сравнения, не может быть {@code null}
     * @param card2 вторая карта для сравнения, не может быть {@code null}
     * @return отрицательное число, если card1 должна идти перед card2;
     *         положительное число, если card1 должна идти после card2;
     *         0, если карты равны по порядку
     * @throws NullPointerException если {@code card1} или {@code card2} равны {@code null}
     */
    @Override
    public int compare(Card card1, Card card2) {
        // Сначала сортируем по масти (козыри в начале)
        int suitComparison = compareSuits(card1.getSuit(), card2.getSuit());
        if (suitComparison != 0) {
            return suitComparison;
        }
        
        // Если масть одинаковая, сортируем по достоинству (по возрастанию)
        return Integer.compare(card1.getValue(), card2.getValue());
    }
    
    /**
     * Сравнивает две масти с учетом козырной масти.
     * 
     * @param suit1 первая масть для сравнения, не может быть {@code null}
     * @param suit2 вторая масть для сравнения, не может быть {@code null}
     * @return отрицательное число, если suit1 должна идти перед suit2;
     *         положительное число, если suit1 должна идти после suit2;
     *         0, если масти равны по порядку
     */
    private int compareSuits(Suit suit1, Suit suit2) {
        if (suit1 == trumpSuit && suit2 != trumpSuit) {
            return -1; // suit1 (козырь) идет раньше
        } else if (suit1 != trumpSuit && suit2 == trumpSuit) {
            return 1; // suit2 (козырь) идет раньше
        } else {
            // Обе козыри или обе не козыри - сортируем по порядку в enum
            return suit1.ordinal() - suit2.ordinal();
        }
    }
}
