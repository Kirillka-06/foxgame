package ru.kirill.foxgame.view;


import ru.kirill.foxgame.logic.FoxGame;
import ru.kirill.foxgame.model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * Главное окно игры.
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
    
    public void updateView() {
        statusPanel.update();
        gamePanel.update();
        infoPanel.update();
        controlPanel.update();
        repaint();
    }

    // Добавляем методы для установки слушателей
    public void setCardClickListener(ActionListener listener) {
        this.cardClickListener = listener;
        if (gamePanel != null) {
            gamePanel.setCardClickListener(listener);
        }
    }
    
    public void setNewGameListener(ActionListener listener) {
        this.newGameListener = listener;
        if (controlPanel != null) {
            controlPanel.setNewGameListener(listener);
        }
    }
    
    public void setRulesListener(ActionListener listener) {
        this.rulesListener = listener;
        if (controlPanel != null) {
            controlPanel.setRulesListener(listener);
        }
    }
    
    // public void setCardClickListener(ActionListener listener) {
    //     gamePanel.setCardClickListener(listener);
    // }
    
    // public void setNewGameListener(ActionListener listener) {
    //     controlPanel.setNewGameListener(listener);
    // }
    
    // public void setRulesListener(ActionListener listener) {
    //     controlPanel.setRulesListener(listener);
    // }
    
    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
    
    public Card getSelectedCard() {
        return gamePanel.getSelectedCard();
    }
    
    public void clearSelection() {
        gamePanel.clearSelection();
    }
}
