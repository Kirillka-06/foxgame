package ru.kirill.foxgame.model;


import ru.kirill.foxgame.logic.FoxGame;


/**
 * Представляет результат одного круга (взятки) в игре "Лисица на опушке".
 * Содержит информацию о победителе, сыгранных картах и набранных очках.
 * 
 * <p>Этот класс используется для хранения истории кругов и отображения
 * результатов в пользовательском интерфейсе.
 * 
 * @see FoxGame
 * @see GameState
 */
public class TrickResult {
    private final Player winner;
    private final Card leadCard;
    private final Card responseCard;
    private final int pointsEarned;
    
    /**
     * Создает результат круга.
     * 
     * @param winner игрок, выигравший круг, не может быть {@code null}
     * @param leadCard карта, которую сыграл ведущий, не может быть {@code null}
     * @param responseCard карта, которую сыграл отвечающий, не может быть {@code null}
     * @param pointsEarned очки, заработанные в этом круге (обычно 0 для отдельных кругов)
     * @throws NullPointerException если любой из параметров равен {@code null}
     */
    public TrickResult(Player winner, Card leadCard, Card responseCard, int pointsEarned) {
        this.winner = winner;
        this.leadCard = leadCard;
        this.responseCard = responseCard;
        this.pointsEarned = pointsEarned;
    }
    
    /**
     * Возвращает игрока, выигравшего круг.
     * 
     * @return победитель круга
     */
    public Player getWinner() { 
        return winner; 
    }
    
    /**
     * Возвращает карту, которую сыграл ведущий.
     * 
     * @return карта ведущего
     */
    public Card getLeadCard() { 
        return leadCard; 
    }
    
    /**
     * Возвращает карту, которую сыграл отвечающий.
     * 
     * @return карта отвечающего
     */
    public Card getResponseCard() { 
        return responseCard; 
    }
    
    /**
     * Возвращает очки, заработанные в этом круге.
     * 
     * @return количество очков
     */
    public int getPointsEarned() { 
        return pointsEarned; 
    }
    
    /**
     * Возвращает строковое представление результата круга.
     * Формат: "ПОБЕДИТЕЛЬ выиграл круг с КАРТА_ВЕДУЩЕГО против КАРТА_ОТВЕЧАЮЩЕГО"
     * 
     * @return строковое представление результата круга
     */
    @Override
    public String toString() {
        return winner + " выиграл круг с " + leadCard + " против " + responseCard;
    }
}
