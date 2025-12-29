package ru.kirill.foxgame.logic;


import ru.kirill.foxgame.model.*;


/**
 * Вспомогательный класс, содержащий статические методы для проверки правил игры.
 * 
 * <p>Этот класс предоставляет методы для проверки допустимости ходов,
 * подсчета очков и других операций, связанных с правилами игры.
 * 
 * @see FoxGame
 * @see GameState
 */
public class GameRules {
    
    /**
     * Проверяет, может ли игрок сыграть указанную карту в текущей ситуации.
     *
     * @param player игрок, делающий ход
     * @param card карта, которую хочет сыграть игрок
     * @param state текущее состояние игры
     * @param isLeading true, если игрок является ведущим в этом круге
     * @return true, если ход допустим по правилам игры
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
     * Подсчитывает очки за количество выигранных кругов согласно таблице.
     * 
     * <p>Таблица очков:
     * <ul>
     *   <li>0-3 побед: 6 очков (Скромный)</li>
     *   <li>4 побед: 1 очко (Побеждённый)</li>
     *   <li>5 побед: 2 очка</li>
     *   <li>6 побед: 3 очка</li>
     *   <li>7-9 побед: 6 очков (Победитель)</li>
     *   <li>10-13 побед: 0 очков (Жадный)</li>
     * </ul>
     * 
     * @param tricksWon количество выигранных кругов (0-13)
     * @return количество очков по таблице
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
     * Возвращает название титула по количеству выигранных кругов.
     * 
     * <p>Титулы по количеству побед:
     * <ul>
     *   <li>0-3: "Скромный"</li>
     *   <li>4-6: "Побеждённый"</li>
     *   <li>7-9: "Победитель"</li>
     *   <li>10-13: "Жадный"</li>
     * </ul>
     * 
     * @param tricksWon количество выигранных кругов
     * @return название титула (Скромный, Побеждённый, Победитель, Жадный)
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
     * Учитывает эффект Ведьмы: если она одна на столе, то становится козырем.
     * 
     * @param card карта для проверки, не может быть {@code null}
     * @param trumpSuit текущая козырная масть, может быть {@code null}
     * @param witchEffectActive {@code true} если эффект Ведьмы активен
     * @param cardInQuestion карта, которая может быть Ведьмой, может быть {@code null}
     * @return {@code true} если карта является козырем, {@code false} в противном случае
     * @throws NullPointerException если {@code card} равен {@code null}
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
     * 
     * <p>Правила сравнения:
     * <ol>
     *   <li>Козырная карта всегда сильнее некозырной</li>
     *   <li>Если обе карты козырные, сильнее та, у которой больше достоинство</li>
     *   <li>Если обе карты некозырные и одной масти, сильнее та, у которой больше достоинство</li>
     *   <li>Если обе карты некозырные разных мастей, первая карта считается сильнее</li>
     * </ol>
     * 
     * @param card1 первая карта для сравнения, не может быть {@code null}
     * @param card2 вторая карта для сравнения, не может быть {@code null}
     * @param trumpSuit козырная масть, может быть {@code null}
     * @return отрицательное число, если card1 слабее card2;
     *         положительное число, если card1 сильнее card2;
     *         0, если карты равны по силе
     * @throws NullPointerException если {@code card1} или {@code card2} равны {@code null}
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
