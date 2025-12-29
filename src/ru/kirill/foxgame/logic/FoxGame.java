package ru.kirill.foxgame.logic;


import ru.kirill.foxgame.controller.GameController;
import ru.kirill.foxgame.model.*;
import java.util.*;


/**
 * Основной класс, управляющий логикой игры "Лисица на опушке".
 * Обрабатывает все игровые механики, ходы игроков, эффекты карт и подсчет очков.
 * 
 * <p>Этот класс представляет собой ядро игры и содержит всю бизнес-логику.
 * Он взаимодействует с моделью ({@link GameState}) и управляется контроллером
 * ({@link GameController}).
 * 
 * @see GameState
 * @see GameController
 */
public class FoxGame {
    private GameState state;
    private GamePhase currentPhase;
    private final Random random = new Random();
    private List<TrickResult> trickHistory;
    private boolean effectAppliedThisRound;
    private Card discardedWoodcutterCard;

    /**
     * Создает новую игру и инициализирует начальное состояние.
     */
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
     * В кону разыгрывается 13 кругов, после чего подсчитываются очки.
     * 
     * <p>В начале кона:
     * <ol>
     *   <li>Сбрасывается счетчик побед в кону</li>
     *   <li>Определяется новый сдающий</li>
     *   <li>Раздаются карты</li>
     *   <li>Определяется козырная карта</li>
     *   <li>Устанавливается первый ведущий (соперник сдающего)</li>
     * </ol>
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
     * Раздает карты игрокам и формирует колоду.
     * 
     * <p>Процесс раздачи:
     * <ol>
     *   <li>Создается полная колода из 33 карт (3 масти × 11 достоинств)</li>
     *   <li>Колода перемешивается</li>
     *   <li>Каждому игроку раздается по 13 карт</li>
     *   <li>Из оставшихся 7 карт формируется колода</li>
     *   <li>Верхняя карта колоды становится козырной</li>
     * </ol>
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
     * Обрабатывает ход игрока: разыгрывает карту с учетом правил и эффектов.
     *
     * @param player игрок, делающий ход
     * @param card карта, которую играет игрок
     * @return true, если ход успешен, false если ход недопустим
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
     * Применяет эффект карты в соответствии с её достоинством.
     * 
     * <p>Вызывается в момент разыгрывания карты игроком.
     * Определяет, какой эффект соответствует карте, и вызывает соответствующий
     * метод обработки эффекта.
     * 
     * <p><b>Особые эффекты по достоинствам карт:</b>
     * <ul>
     *   <li><b>1 (Лебедь)</b>: {@link #applySwanEffect(Card, Player)}</li>
     *   <li><b>3 (Лиса)</b>: {@link #applyFoxEffect(Card, Player)}</li>
     *   <li><b>5 (Дровосек)</b>: {@link #applyWoodcutterEffect(Card, Player)}</li>
     *   <li><b>7 (Страж)</b>: {@link #applyGuardEffect(Card, Player)}</li>
     *   <li><b>9 (Ведьма)</b>: {@link #applyWitchEffect(Card, Player)}</li>
     *   <li><b>11 (Лунатик)</b>: {@link #applyLunaticEffect(Card, Player)}</li>
     * </ul>
     * Четные карты (2, 4, 6, 8, 10) не имеют эффектов.
     * 
     * @param card карта, эффект которой нужно применить, не может быть {@code null}
     * @param player игрок, разыгравший карту, не может быть {@code null}
     * @throws NullPointerException если {@code card} или {@code player} равны {@code null}
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
     * Применяет эффект карты "Лебедь" (достоинство 1).
     * Особое правило: если оба игрока сыграли карты достоинством 1,
     * следующим ведущим становится проигравший текущий круг.
     * Эффект обрабатывается при завершении круга в методе {@link #determineNextLeader}.
     * 
     * @param card карта "Лебедь", которая была разыграна
     * @param player игрок, разыгравший карту "Лебедь"
     * 
     * @see #determineNextLeader(Player, Card, Card)
     * @see Rank#ONE
     */
    private void applySwanEffect(Card card, Player player) {
        // Эффект применяется при завершении круга
        // Храним информацию о сыгранной единице
    }
    
    /**
     * Применяет эффект карты "Лиса" (достоинство 3).
     * Меняет козырную масть на масть сыгранной карты.
     * Эффект применяется немедленно и влияет на определение победителя текущего круга.
     * 
     * @param card карта "Лиса", которая была разыграна
     * @param player игрок, разыгравший карту "Лиса"
     * 
     * @see Rank#THREE
     * @see GameState#setTrumpCard(Card)
     */
    private void applyFoxEffect(Card card, Player player) {
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
     * Применяет эффект карты "Дровосек" (достоинство 5).
     * Заставляет соперника сбросить случайную карту из своей руки.
     * Сброшенная карта удаляется из игры и не участвует в дальнейших кругах.
     * 
     * @param card карта "Дровосек", которая была разыграна
     * @param player игрок, разыгравший карту "Дровосек"
     * 
     * @see Rank#FIVE
     * @see GameState#removeCardFromHand(Player, Card)
     */
    private void applyWoodcutterEffect(Card card, Player player) {
        Player opponent = player.opponent();
        List<Card> opponentHand = state.getHand(opponent);
        
        if (!opponentHand.isEmpty()) {
            int index = random.nextInt(opponentHand.size());
            discardedWoodcutterCard = opponentHand.get(index);
            state.removeCardFromHand(opponent, discardedWoodcutterCard);
        }
    }
    
    /**
     * Применяет эффект карты "Ведьма" (достоинство 9).
     * Помечает, что в текущем круге сыграна карта "Ведьма".
     * Если в круге сыграна только одна "Ведьма", она становится козырем.
     * Если сыграны две "Ведьмы", эффект нейтрализуется.
     * 
     * @param card карта "Ведьма", которая была разыграна
     * @param player игрок, разыгравший карту "Ведьма"
     * 
     * @see Rank#NINE
     * @see #determineRoundWinnerWithEffects(Card, Card, Suit)
     */
    private void applyWitchEffect(Card card, Player player) {
        state.setWitchEffectActive(true);
    }
    
    /**
     * Применяет эффект карты "Страж" (достоинство 7).
     * Блокирует эффекты других особых карт в текущем круге.
     * Если "Страж" сыгран отвечающим, он блокирует эффект карты ведущего.
     * 
     * @param card карта "Страж", которая была разыграна
     * @param player игрок, разыгравший карту "Страж"
     * 
     * @see Rank#SEVEN
     */
    private void applyGuardEffect(Card card, Player player) {
        if (state.isWaitingForResponse()) {
            effectAppliedThisRound = false;
        }
    }
    
    /**
     * Применяет эффект карты "Лунатик" (достоинство 11).
     * Заставляет игроков обменяться случайными картами из своих рук.
     * Количество обмениваемых карт: минимум из 3 и количества карт в руках игроков.
     * Эффект применяется только когда карту разыгрывает ведущий игрок.
     * 
     * @param card карта "Лунатик", которая была разыграна
     * @param player игрок, разыгравший карту "Лунатик"
     * 
     * @see Rank#ELEVEN
     * @see #exchangeRandomCards(Player)
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
     * Игроки обмениваются до 3 случайными картами (или меньше, если у кого-то меньше карт).
     * 
     * @param player игрок, разыгравший карту "Лунатик"
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
     * Определяет победителя круга, обновляет счетчики побед и готовит игру к следующему кругу.
     * 
     * <p>В процессе завершения круга:
     * <ol>
     *   <li>Определяется победитель круга с учетом эффектов карт</li>
     *   <li>Определяется следующий ведущий</li>
     *   <li>Обновляются счетчики побед игроков</li>
     *   <li>Результат круга сохраняется в историю</li>
     *   <li>Проверяется, не завершен ли кон (13 кругов сыграно)</li>
     * </ol>
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
     * Определяет победителя круга с учетом всех эффектов карт.
     * 
     * @param lead карта ведущего игрока, не может быть {@code null}
     * @param response карта отвечающего игрока, не может быть {@code null}
     * @param trumpSuit текущая козырная масть, может быть {@code null}
     * @return победитель круга
     * @throws NullPointerException если {@code lead} или {@code response} равны {@code null}
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
     * Определяет, какой игрок сыграл указанную карту.
     * 
     * @param card карта, для которой нужно определить игрока, не может быть {@code null}
     * @return игрок, сыгравший эту карту, или {@code null} если карта не распознана
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
     * Определяет следующего ведущего с учетом эффектов карт.
     * 
     * <p>Учитывает эффект Лебедя: если оба игрока сыграли карты достоинством 1,
     * ведущим становится проигравший текущий круг.
     * 
     * @param roundWinner победитель текущего круга, не может быть {@code null}
     * @param lead карта ведущего, не может быть {@code null}
     * @param response карта отвечающего, не может быть {@code null}
     * @return следующий ведущий игрок
     * @throws NullPointerException если любой из параметров равен {@code null}
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
     * 
     * <p>В процессе завершения кона:
     * <ol>
     *   <li>Подсчитываются очки для каждого игрока по таблице очков</li>
     *   <li>Очки добавляются к общему счету</li>
     *   <li>Проверяется, достиг ли кто-либо из игроков 21 очка</li>
     *   <li>Если да - игра завершается, иначе начинается новый кон</li>
     * </ol>
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
     * Устанавливает фазу игры в {@link GamePhase#GAME_OVER} и помечает игру как завершенную.
     */
    private void endGame() {
        currentPhase = GamePhase.GAME_OVER;
        state.setGameOver(true);
    }
    
    /**
     * Проверяет, является ли ход допустимым.
     * 
     * <p>Ход считается допустимым, если:
     * <ol>
     *   <li>Игрок делает ход в свой ход</li>
     *   <li>Карта есть у игрока на руке</li>
     *   <li>Для отвечающего: если есть карта ведущей масти, должен играть ее</li>
     * </ol>
     * 
     * @param player игрок, делающий ход, не может быть {@code null}
     * @param card карта, которую хочет сыграть игрок, не может быть {@code null}
     * @return {@code true} если ход допустим, {@code false} в противном случае
     * @throws NullPointerException если {@code player} или {@code card} равны {@code null}
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
     * Возвращает текущее козырную масть с учетом активных эффектов.
     *
     * @return текущая козырная масть
     */
    public Suit getCurrentTrumpSuit() {
        if (state.getNewTrumpFromEffect() != null) {
            return state.getNewTrumpFromEffect().getSuit();
        }
        return state.getTrumpSuit();
    }
    
    /**
     * Возвращает список допустимых ходов для указанного игрока.
     *
     * @param player игрок, для которого нужно получить допустимые ходы
     * @return список карт, которые игрок может легально сыграть
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
     * Возвращает текстовую сводку текущего счета игры.
     * 
     * @return строка с информацией о счете обоих игроков
     */
    public String getScoreSummary() {
        return String.format("Игрок 1: %d очков (%d побед)\nИгрок 2: %d очков (%d побед)",
                state.getPlayer1Score(), state.getPlayer1Tricks(),
                state.getPlayer2Score(), state.getPlayer2Tricks());
    }
    
    /**
     * Возвращает описание текущего состояния игры.
     * 
     * @return строка с информацией о текущей фазе, игроке, круге и козыре
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
    
    /**
     * Возвращает текущее состояние игры (защищенную копию).
     *
     * @return копия текущего состояния игры
     */
    public GameState getState() {
        return state.copy(); // Возвращаем копию для безопасности
    }
    
    public GamePhase getCurrentPhase() {
        return currentPhase;
    }
    
    /**
     * Возвращает текущего игрока (чей ход сейчас).
     *
     * @return текущий игрок
     */
    public Player getCurrentPlayer() {
        return state.getCurrentPlayer();
    }
    
    /**
     * Возвращает текущую козырную карту.
     *
     * @return козырная карта
     */
    public Card getTrumpCard() {
        return state.getTrumpCard();
    }
    
    /**
     * Проверяет, завершена ли игра.
     *
     * @return true, если игра завершена (кто-то набрал 21+ очков)
     */
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
            return null;
        }
    }
    
    /**
     * Возвращает историю всех сыгранных кругов в текущем кону.
     *
     * @return список результатов кругов
     */
    public List<TrickResult> getTrickHistory() {
        return new ArrayList<>(trickHistory);
    }
    
    /**
     * Проверяет, ожидает ли игра ответа от отвечающего игрока.
     *
     * @return true, если ведущий сыграл карту и ожидается ответ
     */
    public boolean isWaitingForResponse() {
        return state.isWaitingForResponse();
    }
    
    /**
     * Возвращает карту, которую сыграл ведущий в текущем круге.
     *
     * @return карта ведущего или null, если круг еще не начат
     */
    public Card getLeadCard() {
        return state.getLeadCard();
    }
    
    /**
     * Возвращает карту, которую сыграл отвечающий в текущем круге.
     *
     * @return карта отвечающего или null, если ответ еще не дан
     */
    public Card getResponseCard() {
        return state.getResponseCard();
    }
    
    /**
     * Возвращает карту, сброшенную эффектом Дровосека.
     * 
     * @return сброшенная карта или {@code null}, если эффект Дровосека не применялся
     */
    public Card getDiscardedWoodcutterCard() {
        return discardedWoodcutterCard;
    }
    
    /**
     * Проверяет, был ли применен эффект карты в текущем круге.
     * 
     * @return {@code true} если в текущем круге был применен эффект карты,
     *         {@code false} в противном случае
     */
    public boolean isEffectAppliedThisRound() {
        return effectAppliedThisRound;
    }
}
