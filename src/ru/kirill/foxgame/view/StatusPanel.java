package ru.kirill.foxgame.view;


import ru.kirill.foxgame.logic.FoxGame;
import ru.kirill.foxgame.model.*;
import javax.swing.*;
import java.awt.*;

/**
 * Панель статуса игры (верхняя панель).
 */
public class StatusPanel extends JPanel {
    private FoxGame game;
    private JLabel player1ScoreLabel;
    private JLabel player2ScoreLabel;
    private JLabel currentPlayerLabel;
    private JLabel phaseLabel;
    private JLabel tricksLabel;
    
    public StatusPanel(FoxGame game) {
        this.game = game;
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new GridLayout(1, 5, 10, 0));
        setBackground(new Color(70, 130, 180));
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        // Счет игрока 1
        JPanel player1Panel = createScorePanel("Игрок 1", Player.PLAYER_1);
        player1ScoreLabel = (JLabel) ((JPanel) player1Panel.getComponent(1)).getComponent(0);
        
        // Счет игрока 2
        JPanel player2Panel = createScorePanel("Игрок 2", Player.PLAYER_2);
        player2ScoreLabel = (JLabel) ((JPanel) player2Panel.getComponent(1)).getComponent(0);
        
        // Текущий игрок
        JPanel currentPlayerPanel = createInfoPanel("Текущий ход", "");
        currentPlayerLabel = (JLabel) ((JPanel) currentPlayerPanel.getComponent(1)).getComponent(0);
        
        // Фаза игры
        JPanel phasePanel = createInfoPanel("Фаза", "");
        phaseLabel = (JLabel) ((JPanel) phasePanel.getComponent(1)).getComponent(0);
        
        // Победы в кону
        JPanel tricksPanel = createInfoPanel("Победы в кону", "");
        tricksLabel = (JLabel) ((JPanel) tricksPanel.getComponent(1)).getComponent(0);
        
        add(player1Panel);
        add(player2Panel);
        add(currentPlayerPanel);
        add(phasePanel);
        add(tricksPanel);
        
        update();
    }
    
    private JPanel createScorePanel(String playerName, Player player) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setOpaque(false);
        
        JLabel nameLabel = new JLabel(playerName);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(player == Player.PLAYER_1 ? new Color(255, 215, 0) : new Color(173, 216, 230));
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel scorePanel = new JPanel();
        scorePanel.setOpaque(false);
        
        JLabel scoreLabel = new JLabel("0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        scoreLabel.setForeground(Color.WHITE);
        
        scorePanel.add(scoreLabel);
        
        panel.add(nameLabel, BorderLayout.NORTH);
        panel.add(scorePanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createInfoPanel(String title, String value) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(new Color(230, 230, 230));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel valuePanel = new JPanel();
        valuePanel.setOpaque(false);
        
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        valueLabel.setForeground(Color.WHITE);
        
        valuePanel.add(valueLabel);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valuePanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    public void update() {
        // Обновляем счет
        player1ScoreLabel.setText(String.valueOf(game.getState().getPlayer1Score()));
        player2ScoreLabel.setText(String.valueOf(game.getState().getPlayer2Score()));
        
        // Обновляем текущего игрока
        Player currentPlayer = game.getCurrentPlayer();
        if (currentPlayer != null) {
            currentPlayerLabel.setText(currentPlayer.toString());
            currentPlayerLabel.setForeground(currentPlayer == Player.PLAYER_1 ? 
                new Color(255, 215, 0) : new Color(173, 216, 230));
        }
        
        // Обновляем фазу игры
        phaseLabel.setText(game.getCurrentPhase().toString());
        
        // Обновляем количество побед
        int player1Tricks = game.getState().getPlayer1Tricks();
        int player2Tricks = game.getState().getPlayer2Tricks();
        tricksLabel.setText(player1Tricks + " : " + player2Tricks);
        
        // Подсвечиваем активного игрока
        if (currentPlayer == Player.PLAYER_1) {
            player1ScoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
            player2ScoreLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        } else {
            player1ScoreLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
            player2ScoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        }
    }
}
