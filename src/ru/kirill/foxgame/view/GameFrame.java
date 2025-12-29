package ru.kirill.foxgame.view;


import ru.kirill.foxgame.controller.GameController;
import ru.kirill.foxgame.logic.FoxGame;
import ru.kirill.foxgame.model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;


/**
 * Главное окно приложения "Лисица на опушке".
 * Собирает все компоненты интерфейса и предоставляет методы для управления ими.
 * 
 * <p>Окно разделено на четыре основные области:
 * <ul>
 *   <li>Север: {@link StatusPanel} - отображает текущий счет и статус игры</li>
 *   <li>Центр: {@link GamePanel} - отображает игровое поле, карты на столе и руки игроков</li>
 *   <li>Восток: {@link InfoPanel} - показывает историю кругов и активные эффекты</li>
 *   <li>Юг: {@link ControlPanel} - содержит кнопки управления игрой</li>
 * </ul>
 * 
 * @see StatusPanel
 * @see GamePanel
 * @see InfoPanel
 * @see ControlPanel
 * @see GameController
 */
public class GameFrame extends JFrame {
    private FoxGame game;
    private GamePanel gamePanel;
    private ControlPanel controlPanel;
    private StatusPanel statusPanel;
    private InfoPanel infoPanel;

    private ActionListener newGameListener;
    private ActionListener rulesListener;
    private ActionListener cardClickListener;
    
    
    /**
     * Создает главное окно игры.
     *
     * @param game экземпляр игры для отображения
     */
    public GameFrame(FoxGame game) {
        this.game = game;
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Лисица на опушке");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));
        
        // Устанавливаем иконку приложения
        try {
            ImageIcon icon = createIcon("🎴");
            setIconImage(icon.getImage());
        } catch (Exception e) {
            // Иконка не критична
        }
        
        // Создаем панели
        statusPanel = new StatusPanel(game);
        gamePanel = new GamePanel(game);
        controlPanel = new ControlPanel();
        infoPanel = new InfoPanel(game);
        
        // Добавляем панели на форму
        add(statusPanel, BorderLayout.NORTH);
        add(gamePanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.EAST);
        add(controlPanel, BorderLayout.SOUTH);
        
        // Настраиваем размер окна
        setPreferredSize(new Dimension(1200, 800));
        pack();
        
        // Центрируем окно
        setLocationRelativeTo(null);
        
        // Устанавливаем красивый шрифт
        setUIFont(new Font("Segoe UI", Font.PLAIN, 12));
    }

    
    
    // Добавляем метод для установки слушателя выхода
    public void setExitListener(ActionListener listener) {
        if (controlPanel != null) {
            controlPanel.setExitListener(listener);
        }
    }
    
    private ImageIcon createIcon(String emoji) {
        // Создаем иконку из эмодзи (заглушка)
        BufferedImage image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(new Color(70, 130, 180));
        g2d.fillRect(0, 0, 32, 32);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        g2d.drawString(emoji, 4, 24);
        g2d.dispose();
        return new ImageIcon(image);
    }
    
    private void setUIFont(Font font) {
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof Font) {
                UIManager.put(key, font);
            }
        }
    }
    
    /**
     * Обновляет все компоненты интерфейса в соответствии с текущим состоянием игры.
     */
    public void updateView() {
        statusPanel.update();
        gamePanel.update();
        infoPanel.update();
        controlPanel.update();
        repaint();
    }

    /**
     * Устанавливает слушатель для обработки кликов по картам.
     *
     * @param listener слушатель событий ActionListener
     */
    public void setCardClickListener(ActionListener listener) {
        this.cardClickListener = listener;
        if (gamePanel != null) {
            gamePanel.setCardClickListener(listener);
        }
    }
    
    /**
     * Устанавливает слушатель для кнопки "Новая игра".
     *
     * @param listener слушатель событий ActionListener
     */
    public void setNewGameListener(ActionListener listener) {
        this.newGameListener = listener;
        if (controlPanel != null) {
            controlPanel.setNewGameListener(listener);
        }
    }
    
    /**
     * Устанавливает слушатель для кнопки "Правила".
     *
     * @param listener слушатель событий ActionListener
     */
    public void setRulesListener(ActionListener listener) {
        this.rulesListener = listener;
        if (controlPanel != null) {
            controlPanel.setRulesListener(listener);
        }
    }
    
    /**
     * Показывает диалоговое окно с сообщением.
     *
     * @param message текст сообщения для отображения
     * @param title заголовок диалогового окна
     * @param messageType тип сообщения (JOptionPane.INFORMATION_MESSAGE, ERROR_MESSAGE и т.д.)
     */
    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
    
    /**
     * Возвращает выбранную игроком карту.
     *
     * @return выбранная карта или null, если карта не выбрана
     */
    public Card getSelectedCard() {
        return gamePanel.getSelectedCard();
    }
    
    /**
     * Сбрасывает выделение карт.
     */
    public void clearSelection() {
        gamePanel.clearSelection();
    }
}
