package ru.kirill.foxgame.view;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Нижняя панель с кнопками управления.
 */
public class ControlPanel extends JPanel {
    private JButton newGameButton;
    private JButton rulesButton;
    private JButton hintButton;
    private JButton undoButton;
    private JButton exitButton;
    
    public ControlPanel() {
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        setBackground(new Color(240, 248, 255));
        setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(70, 130, 180)));
        
        // Создаем кнопки со стилями
        newGameButton = createStyledButton("Новая игра", "🔄", new Color(50, 205, 50));
        rulesButton = createStyledButton("Правила", "📖", new Color(255, 140, 0));
        hintButton = createStyledButton("Подсказка", "💡", new Color(30, 144, 255));
        undoButton = createStyledButton("Отменить ход", "↩️", new Color(138, 43, 226));
        exitButton = createStyledButton("Выход", "🚪", new Color(220, 20, 60));
        
        // Добавляем кнопки
        add(newGameButton);
        add(rulesButton);
        add(hintButton);
        add(undoButton);
        add(exitButton);
    }
    
    private JButton createStyledButton(String text, String icon, Color color) {
        JButton button = new JButton("<html><center>" + icon + "<br>" + text + "</center></html>");
        
        button.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        // Эффект при наведении
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.brighter());
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
    
    // public void setNewGameListener(ActionListener listener) {
    //     newGameButton.addActionListener(listener);
    // }
    
    // public void setRulesListener(ActionListener listener) {
    //     rulesButton.addActionListener(listener);
    // }
    
    // public void setHintListener(ActionListener listener) {
    //     hintButton.addActionListener(listener);
    // }
    
    // public void setUndoListener(ActionListener listener) {
    //     undoButton.addActionListener(listener);
    // }
    
    // public void setExitListener(ActionListener listener) {
    //     exitButton.addActionListener(listener);
    // }

    // Добавляем методы для установки слушателей
    public void setNewGameListener(ActionListener listener) {
        if (newGameButton != null) {
            newGameButton.addActionListener(listener);
        }
    }
    
    public void setRulesListener(ActionListener listener) {
        if (rulesButton != null) {
            rulesButton.addActionListener(listener);
        }
    }
    
    public void setHintListener(ActionListener listener) {
        if (hintButton != null) {
            hintButton.addActionListener(listener);
        }
    }
    
    public void setUndoListener(ActionListener listener) {
        if (undoButton != null) {
            undoButton.addActionListener(listener);
        }
    }
    
    public void setExitListener(ActionListener listener) {
        if (exitButton != null) {
            exitButton.addActionListener(listener);
        }
    }
    
    public void update() {
        // В будущем можно добавить динамическое обновление состояния кнопок
    }
}
