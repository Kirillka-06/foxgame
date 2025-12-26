package ru.kirill.foxgame.model;

/**
 * Represents the result of a trick (round).
 */
public class TrickResult {
    private final Player winner;
    private final Card leadCard;
    private final Card responseCard;
    private final int pointsEarned;
    
    public TrickResult(Player winner, Card leadCard, Card responseCard, int pointsEarned) {
        this.winner = winner;
        this.leadCard = leadCard;
        this.responseCard = responseCard;
        this.pointsEarned = pointsEarned;
    }
    
    public Player getWinner() { 
        return winner; 
    }
    
    public Card getLeadCard() { 
        return leadCard; 
    }
    
    public Card getResponseCard() { 
        return responseCard; 
    }
    
    public int getPointsEarned() { 
        return pointsEarned; 
    }
    
    @Override
    public String toString() {
        return winner + " выиграл круг с " + leadCard + " против " + responseCard;
    }
}
