package ru.kirill.oop_task2_2.logic;

import ru.kirill.oop_task2_2.model.*;
import java.util.*;

/**
 * Основной класс игры, управляющий всей логикой.
 */
public class FoxGame {
    private GameState state;
    private GamePhase currentPhase;
    private final Random random = new Random();
    private List<TrickResult> trickHistory;
    private boolean effectAppliedThisRound;
    private Card discardedWoodcutterCard;
    
    public FoxGame() {
        this.state = new GameState();
        this.currentPhase = GamePhase.DEALING;
        this.trickHistory = new ArrayList<>();
        this.effectAppliedThisRound = false;
        initializeNewGame();
    }
    
    /**
     * Инициализирует новую игру.
     */
    private void initializeNewGame() {
        state.setPlayer1Score(0);
        state.setPlayer2Score(0);
        state.setGameOver(false);
        startNewCon();
    }
    
    /**
     * Начинает новый кон (раунд).
     */
    public void startNewCon() {
        // Сбрасываем состояние для нового кона
        state.setPlayer1Tricks(0);
        state.setPlayer2Tricks(0);
        state.setCurrentRound(0);
        state.setRoundCompleted(false);
        state.setWitchEffectActive(false);
        state.setNewTrumpFromEffect(null);
        trickHistory.clear();
        effectAppliedThisRound = false;
        
        // Определяем сдающего (для первого кона случайно, затем меняем)
        if (state.getDealer() == null) {
            state.setDealer(random.nextBoolean() ? Player.PLAYER_1 : Player.PLAYER_2);
        } else {
            state.setDealer(state.getDealer().opponent());
        }
        
        currentPhase = GamePhase.DEALING;
        dealCards();
        currentPhase = GamePhase.TRUMP_SELECTION;
        
        // Первый ведущий - соперник сдающего
        state.setCurrentPlayer(state.getDealer().opponent());
    }
    
    /**
     * Раздает карты игрокам.
     */
    private void dealCards() {
        // Создаем полную колоду из 33 карт
        List<Card> allCards = new ArrayList<>();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                allCards.add(new Card(suit, rank));
            }
        }
        
        // Перемешиваем колоду
        Collections.shuffle(allCards, random);
        
        // Раздаем по 13 карт каждому игроку
        List<Card> player1Hand = new ArrayList<>();
        List<Card> player2Hand = new ArrayList<>();
        
        for (int i = 0; i < 26; i += 2) {
            player1Hand.add(allCards.get(i));
            player2Hand.add(allCards.get(i + 1));
        }
        
        state.setPlayer1Hand(player1Hand);
        state.setPlayer2Hand(player2Hand);
        
        // Создаем колоду из оставшихся 7 карт
        Stack<Card> deck = new Stack<>();
        for (int i = 26; i < 33; i++) {
            deck.push(allCards.get(i));
        }
        state.setDeck(deck);
        
        // Устанавливаем козырную карту (верхнюю из колоды)
        if (!deck.isEmpty()) {
            state.setTrumpCard(deck.pop());
        }
        
        currentPhase = GamePhase.PLAYING;
    }
    
    /**
     * Игрок делает ход (играет карту).
     */
    public boolean playCard(Player player, Card card) {
        if (!isValidMove(player, card)) {
            return false;
        }
        
        if (!state.isWaitingForResponse()) {
            // Ведущий играет карту
            state.setLeadCard(card);
            state.removeCardFromHand(player, card);
            
            // Применяем эффект карты (если есть)
            applyCardEffect(card, player);
            
            // Если есть эффект, который требует дополнительных действий, ждем
            if (card.getRank() == Rank.THREE || card.getRank() == Rank.FIVE) {
                // Для лисы и дровосека ждем завершения эффекта
                return true;
            }
            
            state.setWaitingForResponse(true);
            state.setCurrentPlayer(player.opponent());
            
        } else {
            // Отвечающий играет карту
            state.setResponseCard(card);
            state.removeCardFromHand(player, card);
            
            // Применяем эффект карты отвечающего (если есть)
            applyCardEffect(card, player);
            
            // Определяем победителя круга
            completeRound();
            
            state.setWaitingForResponse(false);
        }
        
        return true;
    }
    
    /**
     * Применяет эффект карты.
     */
    private void applyCardEffect(Card card, Player player) {
        if (!card.isOdd()) {
            return; // Только нечетные карты имеют эффекты
        }
        
        CardEffect effect = CardEffect.getEffectForRank(card.getRank());
        
        switch (effect) {
            case SWAN:
                applySwanEffect(card, player);
                break;
            case FOX:
                applyFoxEffect(card, player);
                break;
            case WOODCUTTER:
                applyWoodcutterEffect(card, player);
                break;
            case WITCH:
                applyWitchEffect(card, player);
                break;
            case GUARD:
                applyGuardEffect(card, player);
                break;
            case LUNATIC:
                applyLunaticEffect(card, player);
                break;
            default:
                break;
        }
        
        effectAppliedThisRound = true;
    }
    
    /**
     * Эффект Лебедя (1).
     */
    private void applySwanEffect(Card card, Player player) {
        // Эффект применяется при завершении круга
        // Храним информацию о сыгранной единице
    }
    
    /**
     * Эффект Лисы (3).
     */
    private void applyFoxEffect(Card card, Player player) {
        // Лиса меняет козырь
        // Игрок выбирает новую карту для козыря из своей руки
        // В текущей реализации автоматически выбираем первую доступную карту
        List<Card> hand = state.getHand(player);
        for (Card c : hand) {
            if (!c.equals(card)) {
                state.setNewTrumpFromEffect(c);
                state.setTrumpCard(c);
                break;
            }
        }
    }
    
    /**
     * Эффект Дровосека (5).
     */
    private void applyWoodcutterEffect(Card card, Player player) {
        // Дровосек заставляет соперника сбросить случайную карту
        Player opponent = player.opponent();
        List<Card> opponentHand = state.getHand(opponent);
        
        if (!opponentHand.isEmpty()) {
            // Выбираем случайную карту для сброса
            int index = random.nextInt(opponentHand.size());
            discardedWoodcutterCard = opponentHand.get(index);
            state.removeCardFromHand(opponent, discardedWoodcutterCard);
        }
    }
    
    /**
     * Эффект Ведьмы (9).
     */
    private void applyWitchEffect(Card card, Player player) {
        // Ведьма становится козырем, если она одна на столе
        state.setWitchEffectActive(true);
    }
    
    /**
     * Эффект Стража (7).
     */
    private void applyGuardEffect(Card card, Player player) {
        // Страж блокирует эффекты других карт в этом круге
        if (state.isWaitingForResponse()) {
            // Если стража играет отвечающий, он блокирует эффект карты ведущего
            effectAppliedThisRound = false;
        }
    }
    
    /**
     * Эффект Лунатика (11).
     */
    private void applyLunaticEffect(Card card, Player player) {
        // Лунатика заставляет игроков поменяться несколькими случайными картами
        if (!state.isWaitingForResponse()) {
            // Эффект применяется только когда ведущий играет лунатика
            exchangeRandomCards(player);
        }
    }
    
    /**
     * Обмен случайными картами между игроками (эффект Лунатика).
     */
    private void exchangeRandomCards(Player player) {
        List<Card> playerHand = state.getHand(player);
        List<Card> opponentHand = state.getHand(player.opponent());
        
        if (!playerHand.isEmpty() && !opponentHand.isEmpty()) {
            // Обмениваем до 3 картами (или меньше, если у кого-то меньше карт)
            int exchangeCount = Math.min(3, Math.min(playerHand.size(), opponentHand.size()));
            
            for (int i = 0; i < exchangeCount; i++) {
                int playerIndex = random.nextInt(playerHand.size());
                int opponentIndex = random.nextInt(opponentHand.size());
                
                Card playerCard = playerHand.get(playerIndex);
                Card opponentCard = opponentHand.get(opponentIndex);
                
                // Меняем карты местами
                state.removeCardFromHand(player, playerCard);
                state.removeCardFromHand(player.opponent(), opponentCard);
                
                state.addCardToHand(player, opponentCard);
                state.addCardToHand(player.opponent(), playerCard);
                
                // Обновляем ссылки
                playerHand = state.getHand(player);
                opponentHand = state.getHand(player.opponent());
            }
        }
    }
    
    /**
     * Завершает текущий круг.
     */
    private void completeRound() {
        Card lead = state.getLeadCard();
        Card response = state.getResponseCard();
        Suit trumpSuit = state.getTrumpSuit();
        
        // Определяем победителя круга с учетом эффектов
        Player winner = determineRoundWinnerWithEffects(lead, response, trumpSuit);
        state.setRoundWinner(winner);
        
        // Определяем следующего ведущего с учетом эффектов
        Player nextLeader = determineNextLeader(winner, lead, response);
        
        // Обновляем счет побед
        state.incrementTricks(winner);
        
        // Сохраняем результат круга в историю
        TrickResult trickResult = new TrickResult(winner, lead, response, 0);
        trickHistory.add(trickResult);
        
        // Обновляем состояние
        state.setRoundCompleted(true);
        state.setCurrentPlayer(nextLeader);
        state.setCurrentRound(state.getCurrentRound() + 1);
        
        // Сбрасываем карты на столе
        state.resetRound();
        
        // Проверяем, завершен ли кон
        if (state.getCurrentRound() >= 13) {
            endCon();
        } else {
            // Готовимся к следующему кругу
            effectAppliedThisRound = false;
            discardedWoodcutterCard = null;
        }
    }
    
    /**
     * Определяет победителя круга с учетом всех эффектов.
     */
    private Player determineRoundWinnerWithEffects(Card lead, Card response, Suit trumpSuit) {
        // Учитываем эффект Ведьмы
        if (state.isWitchEffectActive() && lead.getRank() == Rank.NINE && response.getRank() != Rank.NINE) {
            // Только одна ведьма - она становится козырем
            return getPlayerByCard(lead);
        } else if (state.isWitchEffectActive() && response.getRank() == Rank.NINE && lead.getRank() != Rank.NINE) {
            // Только одна ведьма - она становится козырем
            return getPlayerByCard(response);
        } else if (lead.getRank() == Rank.NINE && response.getRank() == Rank.NINE) {
            // Две ведьмы нейтрализуют друг друга
            state.setWitchEffectActive(false);
        }
        
        // Определяем победителя по обычным правилам
        return state.determineRoundWinner(lead, response, trumpSuit);
    }
    
    /**
     * Определяет, какой игрок сыграл карту.
     */
    private Player getPlayerByCard(Card card) {
        if (card.equals(state.getLeadCard())) {
            return state.getCurrentPlayer(); // Ведущий на начало круга
        } else if (card.equals(state.getResponseCard())) {
            return state.getCurrentPlayer().opponent(); // Отвечающий
        }
        return null;
    }
    
    /**
     * Определяет следующего ведущего с учетом эффектов.
     */
    private Player determineNextLeader(Player roundWinner, Card lead, Card response) {
        // Проверяем эффект Лебедя
        if (lead.getRank() == Rank.ONE && response.getRank() == Rank.ONE) {
            // Если оба сыграли единицу, ведущим становится проигравший круг
            return roundWinner.opponent();
        }
        
        // По умолчанию ведущим становится победитель круга
        return roundWinner;
    }
    
    /**
     * Завершает кон и подсчитывает очки.
     */
    private void endCon() {
        currentPhase = GamePhase.ROUND_SCORING;
        
        // Подсчитываем очки для каждого игрока
        int player1Tricks = state.getPlayer1Tricks();
        int player2Tricks = state.getPlayer2Tricks();
        
        int player1Points = state.calculatePointsForTricks(player1Tricks);
        int player2Points = state.calculatePointsForTricks(player2Tricks);
        
        // Добавляем очки к общему счету
        state.setPlayer1Score(state.getPlayer1Score() + player1Points);
        state.setPlayer2Score(state.getPlayer2Score() + player2Points);
        
        // Проверяем, достиг ли кто-то 21 очка
        if (state.getPlayer1Score() >= 21 || state.getPlayer2Score() >= 21) {
            endGame();
        } else {
            // Начинаем новый кон
            startNewCon();
        }
    }
    
    /**
     * Завершает игру.
     */
    private void endGame() {
        currentPhase = GamePhase.GAME_OVER;
        state.setGameOver(true);
    }
    
    /**
     * Проверяет, является ли ход допустимым.
     */
    private boolean isValidMove(Player player, Card card) {
        // Проверяем, что игрок делает ход в свой ход
        if (!state.isWaitingForResponse() && player != state.getCurrentPlayer()) {
            return false;
        }
        
        if (state.isWaitingForResponse() && player != state.getCurrentPlayer().opponent()) {
            return false;
        }
        
        // Проверяем, что карта есть у игрока на руке
        if (!state.getHand(player).contains(card)) {
            return false;
        }
        
        // Для отвечающего: если есть карта ведущей масти, должен играть ее
        if (state.isWaitingForResponse() && state.getLeadCard() != null) {
            Suit leadSuit = state.getLeadCard().getSuit();
            boolean hasLeadSuit = state.hasSuit(player, leadSuit);
            
            if (hasLeadSuit && card.getSuit() != leadSuit) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Возвращает козырную масть с учетом эффектов.
     */
    public Suit getCurrentTrumpSuit() {
        if (state.getNewTrumpFromEffect() != null) {
            return state.getNewTrumpFromEffect().getSuit();
        }
        return state.getTrumpSuit();
    }
    
    /**
     * Получает список доступных ходов для игрока.
     */
    public List<Card> getValidMoves(Player player) {
        List<Card> validMoves = new ArrayList<>();
        List<Card> hand = state.getHand(player);
        
        if (!state.isWaitingForResponse()) {
            // Для ведущего все карты доступны
            validMoves.addAll(hand);
        } else {
            // Для отвечающего должны следовать правилам масти
            Suit leadSuit = state.getLeadCard().getSuit();
            boolean hasLeadSuit = state.hasSuit(player, leadSuit);
            
            if (hasLeadSuit) {
                // Должен играть в масть
                for (Card card : hand) {
                    if (card.getSuit() == leadSuit) {
                        validMoves.add(card);
                    }
                }
            } else {
                // Может играть любую карту
                validMoves.addAll(hand);
            }
        }
        
        return validMoves;
    }
    
    /**
     * Получает текущий счет игры.
     */
    public String getScoreSummary() {
        return String.format("Игрок 1: %d очков (%d побед)\nИгрок 2: %d очков (%d побед)",
                state.getPlayer1Score(), state.getPlayer1Tricks(),
                state.getPlayer2Score(), state.getPlayer2Tricks());
    }
    
    /**
     * Получает описание текущего состояния игры.
     */
    public String getGameStatus() {
        StringBuilder status = new StringBuilder();
        
        status.append("Фаза: ").append(currentPhase).append("\n");
        status.append("Текущий игрок: ").append(state.getCurrentPlayer()).append("\n");
        status.append("Круг: ").append(state.getCurrentRound()).append(" из 13\n");
        status.append("Козырь: ").append(state.getTrumpCard()).append("\n");
        
        if (state.getLeadCard() != null) {
            status.append("Ведущая карта: ").append(state.getLeadCard()).append("\n");
        }
        
        if (state.isWaitingForResponse()) {
            status.append("Ожидание ответа от: ").append(state.getCurrentPlayer().opponent()).append("\n");
        }
        
        return status.toString();
    }
    
    // Геттеры
    public GameState getState() {
        return state.copy(); // Возвращаем копию для безопасности
    }
    
    public GamePhase getCurrentPhase() {
        return currentPhase;
    }
    
    public Player getCurrentPlayer() {
        return state.getCurrentPlayer();
    }
    
    public Card getTrumpCard() {
        return state.getTrumpCard();
    }
    
    public boolean isGameOver() {
        return state.isGameOver();
    }
    
    public Player getWinner() {
        if (!state.isGameOver()) {
            return null;
        }
        
        if (state.getPlayer1Score() > state.getPlayer2Score()) {
            return Player.PLAYER_1;
        } else if (state.getPlayer2Score() > state.getPlayer1Score()) {
            return Player.PLAYER_2;
        } else {
            // Ничья - победитель по очкам в последнем кону
            // В текущей реализации просто возвращаем null
            return null;
        }
    }
    
    public List<TrickResult> getTrickHistory() {
        return new ArrayList<>(trickHistory);
    }
    
    public boolean isWaitingForResponse() {
        return state.isWaitingForResponse();
    }
    
    public Card getLeadCard() {
        return state.getLeadCard();
    }
    
    public Card getResponseCard() {
        return state.getResponseCard();
    }
    
    public Card getDiscardedWoodcutterCard() {
        return discardedWoodcutterCard;
    }
    
    public boolean isEffectAppliedThisRound() {
        return effectAppliedThisRound;
    }
}
