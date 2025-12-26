package ru.kirill.foxgame.logic;

import ru.kirill.foxgame.model.*;
import java.util.Comparator;

/**
 * Компаратор для сортировки карт по масти и достоинству.
 */
public class CardComparator implements Comparator<Card> {
    private final Suit trumpSuit;
    
    public CardComparator(Suit trumpSuit) {
        this.trumpSuit = trumpSuit;
    }
    
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
