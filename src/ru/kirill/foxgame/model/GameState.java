package ru.kirill.foxgame.model;


import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import ru.kirill.foxgame.controller.GameController;
import ru.kirill.foxgame.logic.FoxGame;


/**
 * Представляет полное состояние игры "Лисица на опушке" в любой момент времени.
 * Содержит всю информацию о картах, счете, текущем игроке и других параметрах игры.
 * 
 * <p>Этот класс является центральным хранилищем данных игры. Все изменения состояния
 * происходят через методы этого класса, что обеспечивает целостность данных.
 * 
 * @see FoxGame
 * @see GameController
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
    
    /**
     * Создает новое состояние игры с начальными значениями.
     * Инициализирует пустые коллекции и нулевые значения счетчиков.
     */
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
    
    /**
     * Возвращает колоду карт.
     * 
     * @return колода карт в виде стека
     */
    public Stack<Card> getDeck() { 
        return deck; 
    }
    
    /**
     * Устанавливает колоду карт.
     * 
     * @param deck новая колода карт
     */
    public void setDeck(Stack<Card> deck) { 
        this.deck = deck; 
    }
    
    /**
     * Возвращает руку первого игрока (игрока 1).
     * 
     * @return список карт на руке у игрока 1
     */
    public List<Card> getPlayer1Hand() { 
        return player1Hand; 
    }
    
    /**
     * Возвращает руку второго игрока (игрока 2).
     * 
     * @return список карт на руке у игрока 2
     */
    public List<Card> getPlayer2Hand() { 
        return player2Hand; 
    }
    
    /**
     * Возвращает руку указанного игрока.
     *
     * @param player игрок, чью руку нужно получить
     * @return список карт на руке игрока
     */
    public List<Card> getHand(Player player) {
        return player == Player.PLAYER_1 ? player1Hand : player2Hand;
    }
    
    /**
     * Устанавливает руку первого игрока (игрока 1).
     * 
     * @param hand новая рука для игрока 1
     */
    public void setPlayer1Hand(List<Card> hand) { 
        this.player1Hand = hand; 
    }
    
    /**
     * Устанавливает руку второго игрока (игрока 2).
     * 
     * @param hand новая рука для игрока 2
     */
    public void setPlayer2Hand(List<Card> hand) { 
        this.player2Hand = hand; 
    }
    
    /**
     * Возвращает текущую козырную карту.
     * 
     * @return козырная карта или {@code null}, если козырь еще не определен
     */
    public Card getTrumpCard() { 
        return trumpCard; 
    }
    
    /**
     * Устанавливает козырную карту.
     * 
     * @param trumpCard новая козырная карта
     */
    public void setTrumpCard(Card trumpCard) { 
        this.trumpCard = trumpCard; 
    }
    
    /**
     * Возвращает текущую козырную масть.
     * Если козырная карта не установлена, возвращает {@code null}.
     * 
     * @return козырная масть или {@code null}
     */
    public Suit getTrumpSuit() {
        return trumpCard != null ? trumpCard.getSuit() : null;
    }
    
    /**
     * Возвращает текущего игрока (чей ход сейчас).
     * 
     * @return текущий игрок
     */
    public Player getCurrentPlayer() { 
        return currentPlayer; 
    }
    
    /**
     * Устанавливает текущего игрока.
     * 
     * @param currentPlayer новый текущий игрок
     */
    public void setCurrentPlayer(Player currentPlayer) { 
        this.currentPlayer = currentPlayer; 
    }
    
    /**
     * Возвращает текущего сдающего карты.
     * 
     * @return сдающий игрок
     */
    public Player getDealer() { 
        return dealer; 
    }
    
    /**
     * Устанавливает сдающего карты.
     * 
     * @param dealer новый сдающий
     */
    public void setDealer(Player dealer) { 
        this.dealer = dealer; 
    }
    
    /**
     * Возвращает номер текущего круга в кону.
     * Круги нумеруются от 1 до 13.
     * 
     * @return номер текущего круга
     */
    public int getCurrentRound() { 
        return currentRound; 
    }
    
    /**
     * Устанавливает номер текущего круга.
     * 
     * @param currentRound новый номер круга
     */
    public void setCurrentRound(int currentRound) { 
        this.currentRound = currentRound; 
    }
    
    /**
     * Возвращает общее количество очков у первого игрока.
     * 
     * @return счет игрока 1
     */
    public int getPlayer1Score() { 
        return player1Score; 
    }
    
    /**
     * Устанавливает общее количество очков у первого игрока.
     * 
     * @param player1Score новый счет игрока 1
     */
    public void setPlayer1Score(int player1Score) { 
        this.player1Score = player1Score; 
    }
    
    /**
     * Возвращает общее количество очков у второго игрока.
     * 
     * @return счет игрока 2
     */
    public int getPlayer2Score() { 
        return player2Score; 
    }
    
    /**
     * Устанавливает общее количество очков у второго игрока.
     * 
     * @param player2Score новый счет игрока 2
     */
    public void setPlayer2Score(int player2Score) { 
        this.player2Score = player2Score; 
    }
    
    /**
     * Возвращает количество очков у указанного игрока.
     * 
     * @param player игрок, чей счет нужно получить
     * @return счет указанного игрока
     */
    public int getScore(Player player) {
        return player == Player.PLAYER_1 ? player1Score : player2Score;
    }
    
    /**
     * Устанавливает количество очков у указанного игрока.
     * 
     * @param player игрок, чей счет нужно установить
     * @param score новое количество очков
     */
    public void setScore(Player player, int score) {
        if (player == Player.PLAYER_1) {
            player1Score = score;
        } else {
            player2Score = score;
        }
    }
    
    /**
     * Возвращает количество кругов, выигранных первым игроком в текущем кону.
     * 
     * @return количество побед игрока 1 в текущем кону
     */
    public int getPlayer1Tricks() { 
        return player1Tricks; 
    }
    
    /**
     * Устанавливает количество кругов, выигранных первым игроком в текущем кону.
     * 
     * @param player1Tricks новое количество побед игрока 1
     */
    public void setPlayer1Tricks(int player1Tricks) { 
        this.player1Tricks = player1Tricks; 
    }
    
    /**
     * Возвращает количество кругов, выигранных вторым игроком в текущем кону.
     * 
     * @return количество побед игрока 2 в текущем кону
     */
    public int getPlayer2Tricks() { 
        return player2Tricks; 
    }
    
    /**
     * Устанавливает количество кругов, выигранных вторым игроком в текущем кону.
     * 
     * @param player2Tricks новое количество побед игрока 2
     */
    public void setPlayer2Tricks(int player2Tricks) { 
        this.player2Tricks = player2Tricks; 
    }
    
    /**
     * Возвращает количество кругов, выигранных указанным игроком в текущем кону.
     * 
     * @param player игрок, чье количество побед нужно получить
     * @return количество побед указанного игрока
     */
    public int getTricks(Player player) {
        return player == Player.PLAYER_1 ? player1Tricks : player2Tricks;
    }
    
    /**
     * Устанавливает количество кругов, выигранных указанным игроком в текущем кону.
     * 
     * @param player игрок, чье количество побед нужно установить
     * @param tricks новое количество побед
     */
    public void setTricks(Player player, int tricks) {
        if (player == Player.PLAYER_1) {
            player1Tricks = tricks;
        } else {
            player2Tricks = tricks;
        }
    }
    
    /**
     * Увеличивает счетчик побед указанного игрока на 1.
     * 
     * @param player игрок, чей счетчик побед нужно увеличить
     */
    public void incrementTricks(Player player) {
        if (player == Player.PLAYER_1) {
            player1Tricks++;
        } else {
            player2Tricks++;
        }
    }
    
    /**
     * Возвращает карту, которую сыграл ведущий в текущем круге.
     * 
     * @return карта ведущего или {@code null}, если круг еще не начат
     */
    public Card getLeadCard() { 
        return leadCard; 
    }
    
    /**
     * Устанавливает карту, которую сыграл ведущий в текущем круге.
     * 
     * @param leadCard карта ведущего
     */
    public void setLeadCard(Card leadCard) { 
        this.leadCard = leadCard; 
    }
    
    /**
     * Возвращает карту, которую сыграл отвечающий в текущем круге.
     * 
     * @return карта отвечающего или {@code null}, если ответ еще не дан
     */
    public Card getResponseCard() { 
        return responseCard; 
    }
    
    /**
     * Устанавливает карту, которую сыграл отвечающий в текущем круге.
     * 
     * @param responseCard карта отвечающего
     */
    public void setResponseCard(Card responseCard) { 
        this.responseCard = responseCard; 
    }
    
    /**
     * Проверяет, завершена ли игра.
     * Игра считается завершенной, когда один из игроков набрал 21 или более очков.
     * 
     * @return {@code true} если игра завершена, {@code false} в противном случае
     */
    public boolean isGameOver() { 
        return gameOver; 
    }
    
    /**
     * Устанавливает статус завершения игры.
     * 
     * @param gameOver новый статус завершения игры
     */
    public void setGameOver(boolean gameOver) { 
        this.gameOver = gameOver; 
    }
    
    /**
     * Проверяет, завершен ли текущий круг.
     * 
     * @return {@code true} если круг завершен, {@code false} в противном случае
     */
    public boolean isRoundCompleted() { 
        return roundCompleted; 
    }
    
    /**
     * Устанавливает статус завершения текущего круга.
     * 
     * @param roundCompleted новый статус завершения круга
     */
    public void setRoundCompleted(boolean roundCompleted) { 
        this.roundCompleted = roundCompleted; 
    }
    
    /**
     * Возвращает победителя текущего круга.
     * 
     * @return победитель круга или {@code null}, если круг еще не завершен
     */
    public Player getRoundWinner() { 
        return roundWinner; 
    }
    
    /**
     * Устанавливает победителя текущего круга.
     * 
     * @param roundWinner победитель круга
     */
    public void setRoundWinner(Player roundWinner) { 
        this.roundWinner = roundWinner; 
    }
    
    /**
     * Проверяет, активен ли эффект Ведьмы в текущем круге.
     * Эффект Ведьмы активируется, когда в круге сыграна карта достоинством 9.
     * 
     * @return {@code true} если эффект Ведьмы активен, {@code false} в противном случае
     */
    public boolean isWitchEffectActive() { 
        return witchEffectActive; 
    }
    
    /**
     * Устанавливает статус активности эффекта Ведьмы.
     * 
     * @param witchEffectActive новый статус активности эффекта Ведьмы
     */
    public void setWitchEffectActive(boolean witchEffectActive) { 
        this.witchEffectActive = witchEffectActive; 
    }
    
    /**
     * Возвращает новую козырную карту, установленную эффектом Лисы.
     * 
     * @return новая козырная карта или {@code null}, если эффект Лисы не применялся
     */
    public Card getNewTrumpFromEffect() { 
        return newTrumpFromEffect; 
    }
    
    /**
     * Устанавливает новую козырную карту, полученную из эффекта Лисы.
     * 
     * @param newTrumpFromEffect новая козырная карта
     */
    public void setNewTrumpFromEffect(Card newTrumpFromEffect) { 
        this.newTrumpFromEffect = newTrumpFromEffect; 
    }
    
    /**
     * Проверяет, ожидает ли игра ответа от отвечающего игрока.
     * 
     * @return {@code true} если ведущий уже сыграл карту и ожидается ответ,
     *         {@code false} в противном случае
     */
    public boolean isWaitingForResponse() { 
        return waitingForResponse; 
    }
    
    /**
     * Устанавливает статус ожидания ответа от отвечающего игрока.
     * 
     * @param waitingForResponse новый статус ожидания ответа
     */
    public void setWaitingForResponse(boolean waitingForResponse) { 
        this.waitingForResponse = waitingForResponse; 
    }
    
    /**
     * Добавляет карту на руку указанного игрока.
     * 
     * @param player игрок, которому добавляется карта
     * @param card карта для добавления
     * @throws NullPointerException если {@code player} или {@code card} равны {@code null}
     */
    public void addCardToHand(Player player, Card card) {
        if (player == Player.PLAYER_1) {
            player1Hand.add(card);
        } else {
            player2Hand.add(card);
        }
    }
    
    /**
     * Удаляет карту из руки указанного игрока.
     * 
     * @param player игрок, у которого удаляется карта
     * @param card карта для удаления
     * @return {@code true} если карта была успешно удалена,
     *         {@code false} если карта не найдена в руке игрока
     * @throws NullPointerException если {@code player} или {@code card} равны {@code null}
     */
    public boolean removeCardFromHand(Player player, Card card) {
        if (player == Player.PLAYER_1) {
            return player1Hand.remove(card);
        } else {
            return player2Hand.remove(card);
        }
    }
    
    /**
     * Проверяет, есть ли у игрока карты указанной масти.
     *
     * @param player игрок для проверки
     * @param suit масть для проверки
     * @return true, если у игрока есть хотя бы одна карта указанной масти
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
     * Проверяет, есть ли у игрока козырные карты.
     *
     * @param player игрок для проверки
     * @param trumpSuit текущая козырная масть
     * @return true, если у игрока есть хотя бы одна козырная карта
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
     * Берет верхнюю карту из колоды.
     *
     * @return верхняя карта колоды или null, если колода пуста
     */
    public Card drawFromDeck() {
        if (!deck.isEmpty()) {
            return deck.pop();
        }
        return null;
    }
    
    /**
     * Подсчитывает очки за количество выигранных кругов согласно таблице очков.
     *
     * @param tricks количество выигранных кругов
     * @return количество очков
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
     * Определяет победителя круга на основе сыгранных карт.
     *
     * @param lead карта ведущего игрока
     * @param response карта отвечающего игрока
     * @param trumpSuit текущая козырная масть
     * @return игрок, выигравший круг
     */
    public Player determineRoundWinner(Card lead, Card response, Suit trumpSuit) {
        Suit leadSuit = lead.getSuit();

        if (lead.isTrump(trumpSuit) && response.isTrump(trumpSuit)) {
            return lead.getValue() > response.getValue() ? 
                   getPlayerByCard(lead) : getPlayerByCard(response);
        }

        if (lead.isTrump(trumpSuit)) {
            return getPlayerByCard(lead);
        }

        if (response.isTrump(trumpSuit)) {
            return getPlayerByCard(response);
        }

        if (lead.getSuit() == leadSuit && response.getSuit() == leadSuit) {
            return lead.getValue() > response.getValue() ? 
                   getPlayerByCard(lead) : getPlayerByCard(response);
        }

        return getPlayerByCard(lead);
    }
    
    /**
     * Вспомогательный метод для определения игрока по сыгранной карте.
     * 
     * @param card карта, для которой нужно определить игрока
     * @return игрок, сыгравший эту карту, или {@code null} если карта не распознана
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
     * Определяет, какой игрок сыграл карту ведущего.
     * 
     * @return игрок, сыгравший карту ведущего
     */
    private Player getPlayerWhoPlayedLead() {
        return currentPlayer;
    }
    
    /**
     * Определяет, какой игрок сыграл карту отвечающего.
     * 
     * @return игрок, сыгравший карту отвечающего
     */
    private Player getPlayerWhoPlayedResponse() {
        return currentPlayer.opponent();
    }
    
    /**
     * Сбрасывает состояние для нового круга.
     * Очищает карты на столе и сбрасывает флаги эффектов.
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
     * Создает глубокую копию текущего состояния игры.
     *
     * @return новая копия состояния игры
     */
    public GameState copy() {
        GameState copy = new GameState();
    
        copy.deck = new Stack<>();
        copy.deck.addAll(this.deck);
        
        copy.player1Hand = new ArrayList<>(this.player1Hand);
        copy.player2Hand = new ArrayList<>(this.player2Hand);

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
     * Проверяет, находится ли игра в валидном состоянии.
     * 
     * @return {@code true} если состояние игры валидно, {@code false} в противном случае
     */
    public boolean isValid() {
        if (currentRound < 0 || currentRound > 13) return false;
        if (player1Tricks < 0 || player2Tricks < 0) return false;
        if (player1Tricks + player2Tricks > 13) return false;
        if (player1Hand.size() > 13 || player2Hand.size() > 13) return false;
        
        return true;
    }
}
