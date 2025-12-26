package ru.kirill.foxgame.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Represents the complete state of the game.
 */
public class GameState {
    private Stack<Card> deck;
    private List<Card> player1Hand;
    private List<Card> player2Hand;
    private Card trumpCard;
    private Player currentPlayer;
    private Player dealer;
    private int currentRound;
    private int player1Score;
    private int player2Score;
    private int player1Tricks;
    private int player2Tricks;
    private Card leadCard;
    private Card responseCard;
    private boolean gameOver;
    private boolean roundCompleted;
    private Player roundWinner;
    private boolean witchEffectActive;
    private Card newTrumpFromEffect;
    private boolean waitingForResponse;
    
    public GameState() {
        this.player1Hand = new ArrayList<>();
        this.player2Hand = new ArrayList<>();
        this.deck = new Stack<>();
        this.currentRound = 0;
        this.player1Score = 0;
        this.player2Score = 0;
        this.player1Tricks = 0;
        this.player2Tricks = 0;
        this.gameOver = false;
        this.roundCompleted = false;
        this.waitingForResponse = false;
    }
    
    // Getters and Setters
    public Stack<Card> getDeck() { 
        return deck; 
    }
    
    public void setDeck(Stack<Card> deck) { 
        this.deck = deck; 
    }
    
    public List<Card> getPlayer1Hand() { 
        return player1Hand; 
    }
    
    public List<Card> getPlayer2Hand() { 
        return player2Hand; 
    }
    
    public List<Card> getHand(Player player) {
        return player == Player.PLAYER_1 ? player1Hand : player2Hand;
    }
    
    public void setPlayer1Hand(List<Card> hand) { 
        this.player1Hand = hand; 
    }
    
    public void setPlayer2Hand(List<Card> hand) { 
        this.player2Hand = hand; 
    }
    
    public Card getTrumpCard() { 
        return trumpCard; 
    }
    
    public void setTrumpCard(Card trumpCard) { 
        this.trumpCard = trumpCard; 
    }
    
    public Suit getTrumpSuit() {
        return trumpCard != null ? trumpCard.getSuit() : null;
    }
    
    public Player getCurrentPlayer() { 
        return currentPlayer; 
    }
    
    public void setCurrentPlayer(Player currentPlayer) { 
        this.currentPlayer = currentPlayer; 
    }
    
    public Player getDealer() { 
        return dealer; 
    }
    
    public void setDealer(Player dealer) { 
        this.dealer = dealer; 
    }
    
    public int getCurrentRound() { 
        return currentRound; 
    }
    
    public void setCurrentRound(int currentRound) { 
        this.currentRound = currentRound; 
    }
    
    public int getPlayer1Score() { 
        return player1Score; 
    }
    
    public void setPlayer1Score(int player1Score) { 
        this.player1Score = player1Score; 
    }
    
    public int getPlayer2Score() { 
        return player2Score; 
    }
    
    public void setPlayer2Score(int player2Score) { 
        this.player2Score = player2Score; 
    }
    
    public int getScore(Player player) {
        return player == Player.PLAYER_1 ? player1Score : player2Score;
    }
    
    public void setScore(Player player, int score) {
        if (player == Player.PLAYER_1) {
            player1Score = score;
        } else {
            player2Score = score;
        }
    }
    
    public int getPlayer1Tricks() { 
        return player1Tricks; 
    }
    
    public void setPlayer1Tricks(int player1Tricks) { 
        this.player1Tricks = player1Tricks; 
    }
    
    public int getPlayer2Tricks() { 
        return player2Tricks; 
    }
    
    public void setPlayer2Tricks(int player2Tricks) { 
        this.player2Tricks = player2Tricks; 
    }
    
    public int getTricks(Player player) {
        return player == Player.PLAYER_1 ? player1Tricks : player2Tricks;
    }
    
    public void setTricks(Player player, int tricks) {
        if (player == Player.PLAYER_1) {
            player1Tricks = tricks;
        } else {
            player2Tricks = tricks;
        }
    }
    
    public void incrementTricks(Player player) {
        if (player == Player.PLAYER_1) {
            player1Tricks++;
        } else {
            player2Tricks++;
        }
    }
    
    public Card getLeadCard() { 
        return leadCard; 
    }
    
    public void setLeadCard(Card leadCard) { 
        this.leadCard = leadCard; 
    }
    
    public Card getResponseCard() { 
        return responseCard; 
    }
    
    public void setResponseCard(Card responseCard) { 
        this.responseCard = responseCard; 
    }
    
    public boolean isGameOver() { 
        return gameOver; 
    }
    
    public void setGameOver(boolean gameOver) { 
        this.gameOver = gameOver; 
    }
    
    public boolean isRoundCompleted() { 
        return roundCompleted; 
    }
    
    public void setRoundCompleted(boolean roundCompleted) { 
        this.roundCompleted = roundCompleted; 
    }
    
    public Player getRoundWinner() { 
        return roundWinner; 
    }
    
    public void setRoundWinner(Player roundWinner) { 
        this.roundWinner = roundWinner; 
    }
    
    public boolean isWitchEffectActive() { 
        return witchEffectActive; 
    }
    
    public void setWitchEffectActive(boolean witchEffectActive) { 
        this.witchEffectActive = witchEffectActive; 
    }
    
    public Card getNewTrumpFromEffect() { 
        return newTrumpFromEffect; 
    }
    
    public void setNewTrumpFromEffect(Card newTrumpFromEffect) { 
        this.newTrumpFromEffect = newTrumpFromEffect; 
    }
    
    public boolean isWaitingForResponse() { 
        return waitingForResponse; 
    }
    
    public void setWaitingForResponse(boolean waitingForResponse) { 
        this.waitingForResponse = waitingForResponse; 
    }
    
    /**
     * Adds a card to a player's hand.
     */
    public void addCardToHand(Player player, Card card) {
        if (player == Player.PLAYER_1) {
            player1Hand.add(card);
        } else {
            player2Hand.add(card);
        }
    }
    
    /**
     * Removes a card from a player's hand.
     */
    public boolean removeCardFromHand(Player player, Card card) {
        if (player == Player.PLAYER_1) {
            return player1Hand.remove(card);
        } else {
            return player2Hand.remove(card);
        }
    }
    
    /**
     * Checks if a player has cards of a specific suit.
     */
    public boolean hasSuit(Player player, Suit suit) {
        List<Card> hand = getHand(player);
        for (Card card : hand) {
            if (card.getSuit() == suit) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks if a player has trump cards.
     */
    public boolean hasTrump(Player player, Suit trumpSuit) {
        List<Card> hand = getHand(player);
        for (Card card : hand) {
            if (card.getSuit() == trumpSuit) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Draws a card from the deck.
     */
    public Card drawFromDeck() {
        if (!deck.isEmpty()) {
            return deck.pop();
        }
        return null;
    }
    
    /**
     * Calculates points based on the number of tricks won.
     */
    public int calculatePointsForTricks(int tricks) {
        if (tricks >= 0 && tricks <= 3) {
            return 6; // Скромный
        } else if (tricks == 4) {
            return 1; // Побеждённый
        } else if (tricks == 5) {
            return 2;
        } else if (tricks == 6) {
            return 3;
        } else if (tricks >= 7 && tricks <= 9) {
            return 6; // Победитель
        } else if (tricks >= 10 && tricks <= 13) {
            return 0; // Жадный
        }
        return 0;
    }
    
    /**
     * Determines the winner of a round based on played cards.
     */
    public Player determineRoundWinner(Card lead, Card response, Suit trumpSuit) {
        Suit leadSuit = lead.getSuit();
        
        // Both cards are trumps
        if (lead.isTrump(trumpSuit) && response.isTrump(trumpSuit)) {
            return lead.getValue() > response.getValue() ? 
                   getPlayerByCard(lead) : getPlayerByCard(response);
        }
        
        // Only lead card is trump
        if (lead.isTrump(trumpSuit)) {
            return getPlayerByCard(lead);
        }
        
        // Only response card is trump
        if (response.isTrump(trumpSuit)) {
            return getPlayerByCard(response);
        }
        
        // Neither is trump, compare in lead suit
        if (lead.getSuit() == leadSuit && response.getSuit() == leadSuit) {
            return lead.getValue() > response.getValue() ? 
                   getPlayerByCard(lead) : getPlayerByCard(response);
        }
        
        // Response card is not in lead suit, lead wins
        return getPlayerByCard(lead);
    }
    
    /**
     * Helper method to find which player played a card.
     */
    private Player getPlayerByCard(Card card) {
        // This is a simplification - in actual game we need to track who played which card
        if (leadCard != null && leadCard.equals(card)) {
            return getPlayerWhoPlayedLead();
        } else if (responseCard != null && responseCard.equals(card)) {
            return getPlayerWhoPlayedResponse();
        }
        return null;
    }
    
    /**
     * Determines which player played the lead card.
     */
    private Player getPlayerWhoPlayedLead() {
        // The player who is current player at the start of the round played the lead card
        // This needs to be tracked separately in actual implementation
        return currentPlayer;
    }
    
    /**
     * Determines which player played the response card.
     */
    private Player getPlayerWhoPlayedResponse() {
        // The opponent of the player who played lead card
        return currentPlayer.opponent();
    }
    
    /**
     * Resets the state for a new round.
     */
    public void resetRound() {
        leadCard = null;
        responseCard = null;
        roundCompleted = false;
        roundWinner = null;
        witchEffectActive = false;
        newTrumpFromEffect = null;
        waitingForResponse = false;
    }
    
    /**
     * Creates a deep copy of the game state for AI or undo functionality.
     */
    public GameState copy() {
        GameState copy = new GameState();
        
        // Deep copy collections
        copy.deck = new Stack<>();
        copy.deck.addAll(this.deck);
        
        copy.player1Hand = new ArrayList<>(this.player1Hand);
        copy.player2Hand = new ArrayList<>(this.player2Hand);
        
        // Copy other fields
        copy.trumpCard = this.trumpCard;
        copy.currentPlayer = this.currentPlayer;
        copy.dealer = this.dealer;
        copy.currentRound = this.currentRound;
        copy.player1Score = this.player1Score;
        copy.player2Score = this.player2Score;
        copy.player1Tricks = this.player1Tricks;
        copy.player2Tricks = this.player2Tricks;
        copy.leadCard = this.leadCard;
        copy.responseCard = this.responseCard;
        copy.gameOver = this.gameOver;
        copy.roundCompleted = this.roundCompleted;
        copy.roundWinner = this.roundWinner;
        copy.witchEffectActive = this.witchEffectActive;
        copy.newTrumpFromEffect = this.newTrumpFromEffect;
        copy.waitingForResponse = this.waitingForResponse;
        
        return copy;
    }
    
    /**
     * Checks if the game is in a valid state.
     */
    public boolean isValid() {
        // Check basic invariants
        if (currentRound < 0 || currentRound > 13) return false;
        if (player1Tricks < 0 || player2Tricks < 0) return false;
        if (player1Tricks + player2Tricks > 13) return false;
        
        // Check that hands don't exceed 13 cards
        if (player1Hand.size() > 13 || player2Hand.size() > 13) return false;
        
        return true;
    }
}
