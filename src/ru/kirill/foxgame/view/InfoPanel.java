package ru.kirill.foxgame.view;


import ru.kirill.foxgame.logic.FoxGame;
import ru.kirill.foxgame.model.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;


/**
 * Боковая панель с дополнительной информацией.
 * Отображает историю кругов, активные эффекты и доступные ходы.
 * 
 * <p>Панель содержит три секции:
 * <ol>
 *   <li>История кругов - показывает результаты всех сыгранных кругов в текущем кону</li>
 *   <li>Активные эффекты - показывает текущие активные эффекты карт</li>
 *   <li>Доступные ходы - показывает карты, которые текущий игрок может сыграть</li>
 * </ol>
 * 
 * @see GameFrame
 * @see FoxGame
 */
public class InfoPanel extends JPanel {
    private FoxGame game;
    private JTextArea historyArea;
    private JTextArea effectArea;
    private JList<String> validMovesList;
    
    /**
     * Создает информационную панель.
     * 
     * @param game экземпляр игры для отображения информации, не может быть {@code null}
     * @throws NullPointerException если {@code game} равен {@code null}
     */
    public InfoPanel(FoxGame game) {
        this.game = game;
        initializeUI();
    }
    
    /**
     * Инициализирует пользовательский интерфейс панели.
     */
    private void initializeUI() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(245, 245, 245));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setPreferredSize(new Dimension(250, 0));
        
        // Панель истории
        add(createHistoryPanel());
        add(Box.createVerticalStrut(10));
        
        // Панель эффектов
        add(createEffectsPanel());
        add(Box.createVerticalStrut(10));
        
        // Панель доступных ходов
        add(createValidMovesPanel());
        
        update();
    }
    
    /**
     * Создает панель истории кругов.
     * 
     * @return панель истории кругов
     */
    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(139, 69, 19), 2),
            "История кругов",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            new Color(139, 69, 19)
        ));
        panel.setBackground(Color.WHITE);
        
        historyArea = new JTextArea(8, 20);
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Consolas", Font.PLAIN, 11));
        historyArea.setLineWrap(true);
        historyArea.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(historyArea);
        scrollPane.setPreferredSize(new Dimension(230, 150));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    /**
     * Создает панель активных эффектов.
     * 
     * @return панель активных эффектов
     */
    private JPanel createEffectsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0, 100, 0), 2),
            "Активные эффекты",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            new Color(0, 100, 0)
        ));
        panel.setBackground(Color.WHITE);
        
        effectArea = new JTextArea(4, 20);
        effectArea.setEditable(false);
        effectArea.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        effectArea.setLineWrap(true);
        effectArea.setWrapStyleWord(true);
        
        panel.add(new JScrollPane(effectArea), BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Создает панель доступных ходов.
     * 
     * @return панель доступных ходов
     */
    private JPanel createValidMovesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
            "Доступные ходы",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            new Color(70, 130, 180)
        ));
        panel.setBackground(Color.WHITE);
        
        validMovesList = new JList<>();
        validMovesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        validMovesList.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        validMovesList.setBackground(new Color(240, 248, 255));
        
        JScrollPane scrollPane = new JScrollPane(validMovesList);
        scrollPane.setPreferredSize(new Dimension(230, 100));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Обновляет всю информацию на панели.
     */
    public void update() {
        // Обновляем историю
        updateHistory();
        
        // Обновляем эффекты
        updateEffects();
        
        // Обновляем доступные ходы
        updateValidMoves();
    }
    
    /**
     * Обновляет отображение истории кругов.
     */
    private void updateHistory() {
        StringBuilder history = new StringBuilder();
        var trickHistory = game.getTrickHistory();
        
        if (trickHistory.isEmpty()) {
            history.append("История кругов пуста\n");
        } else {
            for (int i = 0; i < trickHistory.size(); i++) {
                var trick = trickHistory.get(i);
                history.append(String.format("Круг %d: %s\n", 
                    i + 1, 
                    trick.toString()));
            }
        }
        
        
        historyArea.setText(history.toString());
        historyArea.setCaretPosition(0);
    }
    
    /**
     * Обновляет отображение активных эффектов.
     */
    private void updateEffects() {
        StringBuilder effects = new StringBuilder();
        
        // Исправлено: используем getState().isWitchEffectActive() вместо game.isWitchEffectActive()
        if (game.getState().isWitchEffectActive()) {
            effects.append("• Ведьма активна (карта становится козырем)\n");
        }
        
        if (game.getState().getNewTrumpFromEffect() != null) {
            effects.append("• Козырь изменен Лисой\n");
        }
        
        if (game.getDiscardedWoodcutterCard() != null) {
            effects.append("• Дровосек сбросил карту соперника\n");
        }
        
        if (game.isEffectAppliedThisRound()) {
            effects.append("• В этом круге применен эффект карты\n");
        }
        
        if (effects.length() == 0) {
            effects.append("Нет активных эффектов");
        }
        
        effectArea.setText(effects.toString());
    }
    
    /**
     * Обновляет отображение доступных ходов.
     */
    private void updateValidMoves() {
        Player currentPlayer = game.getCurrentPlayer();
        if (currentPlayer == Player.PLAYER_1) {
            var validMoves = game.getValidMoves(Player.PLAYER_1);
            String[] moves = validMoves.stream()
                .map(card -> card.toString() + 
                    (card.isOdd() ? " (эффект: " + 
                        CardEffect.getEffectForRank(card.getRank()) + ")" : ""))
                .toArray(String[]::new);
            validMovesList.setListData(moves);
        } else {
            validMovesList.setListData(new String[]{"Ход противника"});
        }
    }
}
