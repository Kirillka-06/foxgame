package ru.kirill.foxgame.logic;

import ru.kirill.foxgame.model.*;

/**
 * Вспомогательный класс, содержащий правила игры.
 */
public class GameRules {
    
    /**
     * Проверяет, может ли игрок сыграть карту в текущей ситуации.
     */
    public static boolean canPlayCard(Player player, Card card, 
                                      GameState state, boolean isLeading) {
        
        // Проверяем, что карта есть у игрока
        if (!state.getHand(player).contains(card)) {
            return false;
        }
        
        if (isLeading) {
            // Ведущий может играть любую карту
            return true;
        } else {
            // Отвечающий должен следовать масти, если может
            Card leadCard = state.getLeadCard();
            if (leadCard == null) {
                return false;
            }
            
            Suit leadSuit = leadCard.getSuit();
            boolean hasLeadSuit = state.hasSuit(player, leadSuit);
            
            if (hasLeadSuit) {
                // Должен играть в масть
                return card.getSuit() == leadSuit;
            } else {
                // Может играть любую карту
                return true;
            }
        }
    }
    
    /**
     * Подсчитывает очки за количество побед согласно таблице.
     */
    public static int calculateScore(int tricksWon) {
        if (tricksWon <= 3) {
            return 6; // Скромный
        } else if (tricksWon == 4) {
            return 1; // Побеждённый
        } else if (tricksWon == 5) {
            return 2;
        } else if (tricksWon == 6) {
            return 3;
        } else if (tricksWon >= 7 && tricksWon <= 9) {
            return 6; // Победитель
        } else if (tricksWon >= 10) {
            return 0; // Жадный
        }
        return 0;
    }
    
    /**
     * Возвращает название титула по количеству побед.
     */
    public static String getTitleForTricks(int tricksWon) {
        if (tricksWon <= 3) {
            return "Скромный";
        } else if (tricksWon == 4) {
            return "Побеждённый";
        } else if (tricksWon >= 5 && tricksWon <= 6) {
            return "Побеждённый"; // По таблице 5-6 тоже Побеждённый
        } else if (tricksWon >= 7 && tricksWon <= 9) {
            return "Победитель";
        } else if (tricksWon >= 10) {
            return "Жадный";
        }
        return "Неизвестно";
    }
    
    /**
     * Определяет, является ли карта козырем с учетом эффектов.
     */
    public static boolean isTrumpCard(Card card, Suit trumpSuit, 
                                      boolean witchEffectActive, Card cardInQuestion) {
        
        if (witchEffectActive && cardInQuestion != null && 
            cardInQuestion.getRank() == Rank.NINE) {
            // Ведьма становится козырем
            return card.equals(cardInQuestion);
        }
        
        return card.getSuit() == trumpSuit;
    }
    
    /**
     * Сравнивает две карты с учетом козырной масти.
     */
    public static int compareCards(Card card1, Card card2, Suit trumpSuit) {
        boolean card1IsTrump = card1.getSuit() == trumpSuit;
        boolean card2IsTrump = card2.getSuit() == trumpSuit;
        
        if (card1IsTrump && !card2IsTrump) {
            return 1; // card1 сильнее
        } else if (!card1IsTrump && card2IsTrump) {
            return -1; // card2 сильнее
        } else if (card1IsTrump && card2IsTrump) {
            // Обе козыри - сравниваем по достоинству
            return Integer.compare(card1.getValue(), card2.getValue());
        } else {
            // Обе не козыри - должны быть в одной масти для сравнения
            if (card1.getSuit() == card2.getSuit()) {
                return Integer.compare(card1.getValue(), card2.getValue());
            } else {
                // Разные масти - нельзя сравнить напрямую
                // В контексте игры это означает, что первая карта сильнее
                return 1;
            }
        }
    }
}
