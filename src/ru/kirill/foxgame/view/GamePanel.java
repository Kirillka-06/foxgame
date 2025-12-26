package ru.kirill.foxgame.view;

import ru.kirill.foxgame.logic.FoxGame;
import ru.kirill.foxgame.model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

// package foxgame.view;

// import foxgame.core.FoxGame;
// import foxgame.model.*;
// import javax.swing.*;
// import java.awt.*;
// import java.awt.event.ActionEvent;
// import java.awt.event.ActionListener;
// import java.util.List;

/**
 * Центральная панель игры, отображающая карты на столе и руки игроков.
 */
public class GamePanel extends JPanel {
    private FoxGame game;
    private CardPanel player1HandPanel;
    private CardPanel player2HandPanel;
    private TablePanel tablePanel;
    private JLabel trumpLabel;
    private Card selectedCard;
    
    public GamePanel(FoxGame game) {
        this.game = game;
        this.selectedCard = null;
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(34, 139, 34)); // Зеленый фон как игровой стол
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Панель для верхнего игрока (противник)
        player2HandPanel = new CardPanel("Противник", false);
        player2HandPanel.setBackground(new Color(34, 139, 34, 200));
        
        // Панель для нижнего игрока (текущий игрок)
        player1HandPanel = new CardPanel("Вы", true);
        player1HandPanel.setBackground(new Color(34, 139, 34, 200));
        
        // Центральная панель стола
        tablePanel = new TablePanel();
        
        // Панель козыря
        JPanel trumpPanel = createTrumpPanel();
        
        // Собираем интерфейс
        add(player2HandPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        add(player1HandPanel, BorderLayout.SOUTH);
        add(trumpPanel, BorderLayout.WEST);
        
        // Обновляем данные
        update();
    }
    
    private JPanel createTrumpPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(218, 165, 32, 150));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(139, 69, 19), 2),
            BorderFactory.createEmptyBorder(10, 5, 10, 5)
        ));
        
        trumpLabel = new JLabel("Козырь: ");
        trumpLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        trumpLabel.setForeground(Color.WHITE);
        trumpLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel iconLabel = new JLabel("🃏");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(Box.createVerticalStrut(10));
        panel.add(iconLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(trumpLabel);
        panel.add(Box.createVerticalStrut(10));
        
        panel.setPreferredSize(new Dimension(120, 200));
        
        return panel;
    }
    
    public void update() {
        // Обновляем козырь
        Card trumpCard = game.getTrumpCard();
        if (trumpCard != null) {
            trumpLabel.setText("<html><center>Козырь:<br>" + trumpCard + "</center></html>");
        }
        
        // Обновляем руки игроков
        List<Card> player1Hand = game.getState().getPlayer1Hand();
        List<Card> player2Hand = game.getState().getPlayer2Hand();
        
        player1HandPanel.updateCards(player1Hand, game.getValidMoves(Player.PLAYER_1));
        player2HandPanel.updateCards(player2Hand, null); // У противника карты скрыты
        
        // Обновляем стол
        Card leadCard = game.getLeadCard();
        Card responseCard = game.getResponseCard();
        tablePanel.updateCards(leadCard, responseCard);
        
        // Обновляем состояние выбора карт
        Player currentPlayer = game.getCurrentPlayer();
        player1HandPanel.setEnabled(currentPlayer == Player.PLAYER_1);
        player1HandPanel.setActive(currentPlayer == Player.PLAYER_1);
        
        // Обновляем подсказки
        if (game.isWaitingForResponse()) {
            player1HandPanel.setHint("Отвечайте на карту соперника");
        } else if (currentPlayer == Player.PLAYER_1) {
            player1HandPanel.setHint("Ваш ход - выберите карту");
        } else {
            player1HandPanel.setHint("Ход противника");
        }
    }
    
    public void setCardClickListener(ActionListener listener) {
        player1HandPanel.setCardClickListener(listener);
    }
    
    public Card getSelectedCard() {
        return selectedCard;
    }
    
    public void clearSelection() {
        selectedCard = null;
        player1HandPanel.clearSelection();
    }
    
    // Внутренний класс для панели карт игрока
    private class CardPanel extends JPanel {
        private String playerName;
        private boolean interactive;
        private JLabel nameLabel;
        private JLabel hintLabel;
        private JPanel cardsPanel;
        private List<Card> validMoves;
        private ActionListener cardClickListener;
        
        public CardPanel(String playerName, boolean interactive) {
            this.playerName = playerName;
            this.interactive = interactive;
            initializeUI();
        }
        
        private void initializeUI() {
            setLayout(new BorderLayout(5, 5));
            setOpaque(false);
            
            // Панель с именем игрока
            JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            headerPanel.setOpaque(false);
            
            nameLabel = new JLabel(playerName);
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            nameLabel.setForeground(Color.WHITE);
            
            hintLabel = new JLabel("");
            hintLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            hintLabel.setForeground(Color.YELLOW);
            
            headerPanel.add(nameLabel);
            headerPanel.add(Box.createHorizontalStrut(20));
            headerPanel.add(hintLabel);
            
            // Панель для карт
            cardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
            cardsPanel.setOpaque(false);
            
            JScrollPane scrollPane = new JScrollPane(cardsPanel);
            scrollPane.setOpaque(false);
            scrollPane.getViewport().setOpaque(false);
            scrollPane.setBorder(null);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
            
            add(headerPanel, BorderLayout.NORTH);
            add(scrollPane, BorderLayout.CENTER);
            
            if (interactive) {
                setBorder(BorderFactory.createLineBorder(new Color(255, 215, 0), 2));
            }
        }
        
        public void updateCards(List<Card> cards, List<Card> validMoves) {
            this.validMoves = validMoves;
            cardsPanel.removeAll();
            
            if (cards == null || cards.isEmpty()) {
                JLabel emptyLabel = new JLabel("Карт нет");
                emptyLabel.setForeground(Color.WHITE);
                cardsPanel.add(emptyLabel);
            } else {
                for (Card card : cards) {
                    CardComponent cardComp = new CardComponent(card, interactive);
                    
                    // Если карта недоступна для хода, делаем ее полупрозрачной
                    if (interactive && validMoves != null && !validMoves.contains(card)) {
                        cardComp.setEnabled(false);
                        cardComp.setAlpha(0.5f);
                    }
                    
                    if (interactive && cardClickListener != null) {
                        cardComp.addActionListener(cardClickListener);
                    }
                    
                    cardsPanel.add(cardComp);
                }
            }
            
            cardsPanel.revalidate();
            cardsPanel.repaint();
        }
        
        public void setCardClickListener(ActionListener listener) {
            this.cardClickListener = listener;
        }
        
        public void setHint(String hint) {
            hintLabel.setText(hint);
        }
        
        public void setActive(boolean active) {
            if (active) {
                setBorder(BorderFactory.createLineBorder(new Color(255, 215, 0), 3));
            } else {
                setBorder(BorderFactory.createLineBorder(new Color(128, 128, 128), 2));
            }
        }
        
        public void clearSelection() {
            for (Component comp : cardsPanel.getComponents()) {
                if (comp instanceof CardComponent) {
                    ((CardComponent) comp).setSelected(false);
                }
            }
        }
    }
    
    // Внутренний класс для панели стола
    private class TablePanel extends JPanel {
        private CardComponent leadCardComponent;
        private CardComponent responseCardComponent;
        private JLabel roundLabel;
        private JLabel statusLabel;
        
        public TablePanel() {
            initializeUI();
        }
        
        private void initializeUI() {
            setLayout(new BorderLayout(20, 20));
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            // Панель для информации о круге
            JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
            infoPanel.setOpaque(false);
            
            roundLabel = new JLabel("Круг: 1/13");
            roundLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            roundLabel.setForeground(Color.WHITE);
            
            statusLabel = new JLabel("");
            statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            statusLabel.setForeground(Color.YELLOW);
            
            infoPanel.add(roundLabel);
            infoPanel.add(statusLabel);
            
            // Панель для карт на столе
            JPanel cardsPanel = new JPanel(new GridBagLayout());
            cardsPanel.setOpaque(false);
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 50, 10, 50);
            
            // Карта ведущего
            JPanel leadPanel = createCardSlot("Ведущий");
            leadCardComponent = new CardComponent(null, false);
            leadPanel.add(leadCardComponent);
            
            // Карта отвечающего
            JPanel responsePanel = createCardSlot("Отвечающий");
            responseCardComponent = new CardComponent(null, false);
            responsePanel.add(responseCardComponent);
            
            // Разделитель
            JLabel vsLabel = new JLabel("VS");
            vsLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            vsLabel.setForeground(Color.RED);
            
            gbc.gridx = 0;
            gbc.gridy = 0;
            cardsPanel.add(leadPanel, gbc);
            
            gbc.gridx = 1;
            cardsPanel.add(vsLabel, gbc);
            
            gbc.gridx = 2;
            cardsPanel.add(responsePanel, gbc);
            
            add(infoPanel, BorderLayout.NORTH);
            add(cardsPanel, BorderLayout.CENTER);
        }
        
        private JPanel createCardSlot(String title) {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setOpaque(false);
            
            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            titleLabel.setForeground(Color.WHITE);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            panel.add(titleLabel);
            panel.add(Box.createVerticalStrut(10));
            
            return panel;
        }
        
        public void updateCards(Card leadCard, Card responseCard) {
            leadCardComponent.setCard(leadCard);
            responseCardComponent.setCard(responseCard);
            
            // Обновляем информацию о круге
            int currentRound = game.getState().getCurrentRound();
            roundLabel.setText("Круг: " + currentRound + "/13");
            
            // Обновляем статус
            if (game.isWaitingForResponse()) {
                statusLabel.setText("Ожидание ответа...");
            } else if (leadCard != null && responseCard != null) {
                Player winner = game.getState().getRoundWinner();
                if (winner != null) {
                    statusLabel.setText("Выиграл: " + winner);
                }
            } else {
                statusLabel.setText("");
            }
            
            repaint();
        }
    }
}
