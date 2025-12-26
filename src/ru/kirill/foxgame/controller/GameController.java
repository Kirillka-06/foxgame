package ru.kirill.foxgame.controller;

import ru.kirill.oop_task2_2.logic.FoxGame;
import ru.kirill.oop_task2_2.model.*;
import ru.kirill.oop_task2_2.view.GameFrame;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Основной контроллер, связывающий модель, представление и логику игры.
 */
public class GameController {
    private FoxGame game;
    private GameFrame view;
    private Timer aiTimer;
    private boolean isAITurn;
    private final Random random = new Random();
    
    public GameController(FoxGame game, GameFrame view) {
        this.game = game;
        this.view = view;
        this.aiTimer = new Timer();
        this.isAITurn = false;
        
        initializeController();
        updateView();
    }
    
    private void initializeController() {
        // Настройка слушателя для карт игрока 1
        view.setCardClickListener(new CardClickListener());
        
        // Настройка слушателей для кнопок управления
        view.setNewGameListener(new NewGameListener());
        view.setRulesListener(new RulesListener());
        
        // Инициализация игры
        startNewGame();
    }
    
    /**
     * Запускает новую игру.
     */
    private void startNewGame() {
        // Создаем новую игру
        this.game = new FoxGame();
        isAITurn = false;
        
        // Обновляем представление
        updateView();
        
        // Проверяем, должен ли AI делать первый ход
        if (game.getCurrentPlayer() == Player.PLAYER_2) {
            scheduleAIMove(1000); // AI делает ход через 1 секунду
        }
        
        view.showMessage("Новая игра начата! Первый ход: " + game.getCurrentPlayer(), 
                        "Новая игра", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Обновляет все компоненты представления.
     */
    private void updateView() {
        view.updateView();
    }
    
    /**
     * Обрабатывает ход игрока 1 (человека).
     */
    private void handlePlayerMove(Card card) {
        if (isAITurn) {
            view.showMessage("Сейчас ход противника!", "Не ваш ход", 
                           JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Проверяем, может ли игрок сделать ход
        if (!game.playCard(Player.PLAYER_1, card)) {
            view.showMessage("Недопустимый ход! Выберите другую карту.", 
                           "Ошибка хода", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Обновляем представление
        updateView();
        
        // Проверяем, завершена ли игра
        if (game.isGameOver()) {
            handleGameOver();
            return;
        }
        
        // Если ожидается ответ от AI
        if (game.isWaitingForResponse()) {
            // AI должен ответить
            isAITurn = true;
            scheduleAIMove(1500); // AI отвечает через 1.5 секунды
        } else if (game.getCurrentPlayer() == Player.PLAYER_2) {
            // AI должен вести
            isAITurn = true;
            scheduleAIMove(2000); // AI ведет через 2 секунды
        }
    }
    
    /**
     * Обрабатывает ход AI (игрока 2).
     */
    private void handleAIMove() {
        if (!isAITurn) return;
        
        Player aiPlayer = Player.PLAYER_2;
        List<Card> validMoves = game.getValidMoves(aiPlayer);
        
        if (validMoves.isEmpty()) {
            // Нет доступных ходов (это не должно происходить)
            isAITurn = false;
            return;
        }
        
        // Выбираем карту для AI
        Card aiCard = selectAICard(validMoves);
        
        // Выполняем ход AI
        game.playCard(aiPlayer, aiCard);
        
        // Обновляем представление
        updateView();
        
        // Проверяем, завершена ли игра
        if (game.isGameOver()) {
            handleGameOver();
            return;
        }
        
        isAITurn = false;
        
        // Если теперь ход AI снова (например, AI выиграл круг и ведет следующий)
        if (game.getCurrentPlayer() == Player.PLAYER_2 && !game.isWaitingForResponse()) {
            isAITurn = true;
            scheduleAIMove(1500); // AI делает следующий ход через 1.5 секунды
        }
    }
    
    /**
     * Выбирает карту для AI на основе простой стратегии.
     */
    private Card selectAICard(List<Card> validMoves) {
        if (validMoves.isEmpty()) {
            return null;
        }
        
        // Простая стратегия AI:
        // 1. Сначала ищем карты с эффектами (нечетные)
        // 2. Затем ищем козырные карты
        // 3. Иначе выбираем случайную карту
        
        // Ищем карты с эффектами
        List<Card> effectCards = validMoves.stream()
            .filter(Card::isOdd)
            .toList();
        
        if (!effectCards.isEmpty()) {
            // Предпочитаем сильные эффекты (Лиса, Ведьма, Дровосек)
            for (Card card : effectCards) {
                if (card.getRank() == Rank.THREE || // Лиса
                    card.getRank() == Rank.NINE ||   // Ведьма
                    card.getRank() == Rank.FIVE) {   // Дровосек
                    return card;
                }
            }
            // Возвращаем первую карту с эффектом
            return effectCards.get(0);
        }
        
        // Ищем козырные карты
        Suit trumpSuit = game.getCurrentTrumpSuit();
        List<Card> trumpCards = validMoves.stream()
            .filter(card -> card.getSuit() == trumpSuit)
            .toList();
        
        if (!trumpCards.isEmpty()) {
            // Выбираем самую старшую козырную карту
            Card bestTrump = trumpCards.get(0);
            for (Card card : trumpCards) {
                if (card.getValue() > bestTrump.getValue()) {
                    bestTrump = card;
                }
            }
            return bestTrump;
        }
        
        // Выбираем случайную карту
        return validMoves.get(random.nextInt(validMoves.size()));
    }
    
    /**
     * Планирует ход AI через указанное время.
     */
    private void scheduleAIMove(int delay) {
        aiTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    handleAIMove();
                });
            }
        }, delay);
    }
    
    /**
     * Обрабатывает завершение игры.
     */
    private void handleGameOver() {
        Player winner = game.getWinner();
        String message;
        
        if (winner != null) {
            message = String.format("Игра окончена! Победитель: %s\n\n" +
                    "Итоговый счет:\n" +
                    "Игрок 1: %d очков\n" +
                    "Игрок 2: %d очков\n\n" +
                    "Начните новую игру!",
                    winner,
                    game.getState().getPlayer1Score(),
                    game.getState().getPlayer2Score());
        } else {
            message = "Игра завершилась вничью!";
        }
        
        view.showMessage(message, "Игра окончена", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Показывает правила игры.
     */
    private void showRules() {
        String rules = """
            **Правила игры "Лисица на опушке"**
            
            Состав игры:
            33 карты: 11 карт "Ключ", 11 карт "Колокольчик", 11 карт "Луна"
            Каждая масть пронумерована от 1 до 11.
            
            Цель игры:
            Набрать 21 очко раньше соперника.
            
            Ход игры:
            1. Кон состоит из 13 кругов
            2. В каждом круге игроки разыгрывают по одной карте
            3. Ведущий играет первую карту, задавая масть
            4. Отвечающий должен играть в масть, если это возможно
            5. Круг выигрывает старшая карта ведущей масти или козырная карта
            6. Победитель круга становится ведущим в следующем круге
            
            Особые карты (нечетные):
            1 (Лебедь) - Если оба игрока сыграли 1, ведущим становится проигравший
            3 (Лиса) - Меняет козырную масть
            5 (Дровосек) - Заставляет соперника сбросить случайную карту
            7 (Страж) - Блокирует эффекты других карт
            9 (Ведьма) - Становится козырем, если она одна на столе
            11 (Лунатик) - Меняет случайные карты с соперником
            
            Подсчет очков (в конце кона):
            0-3 побед: 6 очков (Скромный)
            4 побед: 1 очко (Побеждённый)
            5 побед: 2 очка
            6 побед: 3 очка
            7-9 побед: 6 очков (Победитель)
            10-13 побед: 0 очков (Жадный)
            
            Игра заканчивается, когда один из игроков набирает 21 очко.
            """;
        
        view.showMessage(rules, "Правила игры", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Внутренние классы-слушатели
    
    /**
     * Слушатель для кликов по картам.
     */
    private class CardClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof ru.kirill.oop_task2_2.view.CardComponent) {
                ru.kirill.oop_task2_2.view.CardComponent cardComponent = (ru.kirill.oop_task2_2.view.CardComponent) e.getSource();
                Card card = cardComponent.getCard();
                
                if (card != null) {
                    handlePlayerMove(card);
                }
            }
        }
    }
    
    /**
     * Слушатель для кнопки "Новая игра".
     */
    private class NewGameListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int choice = JOptionPane.showConfirmDialog(
                view,
                "Начать новую игру? Текущий прогресс будет потерян.",
                "Новая игра",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (choice == JOptionPane.YES_OPTION) {
                startNewGame();
            }
        }
    }
    
    /**
     * Слушатель для кнопки "Правила".
     */
    private class RulesListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            showRules();
        }
    }
    
    /**
     * Завершает работу контроллера.
     */
    public void dispose() {
        if (aiTimer != null) {
            aiTimer.cancel();
            aiTimer = null;
        }
    }
}
