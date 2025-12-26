package ru.kirill.foxgame.view;

import ru.kirill.foxgame.model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Компонент для отображения карты.
 */
public class CardComponent extends JButton {
    private Card card;
    private boolean faceUp;
    private boolean selected;
    private float alpha = 1.0f;
    private List<ActionListener> actionListeners = new ArrayList<>();
    
    // Цвета для разных мастей
    private static final Color KEY_COLOR = new Color(184, 134, 11); // Темное золото
    private static final Color BELL_COLOR = new Color(0, 100, 0);   // Темно-зеленый
    private static final Color MOON_COLOR = new Color(72, 61, 139); // Темно-синий
    private static final Color BACK_COLOR = new Color(139, 0, 0);   // Темно-красный
    
    // public CardComponent(Card card, boolean faceUp) {
    //     this.card = card;
    //     this.faceUp = faceUp;
    //     this.selected = false;
        
    //     setPreferredSize(new Dimension(80, 120));
    //     setMinimumSize(new Dimension(70, 105));
    //     setMaximumSize(new Dimension(90, 135));
    //     setBorder(BorderFactory.createEmptyBorder());
    //     setContentAreaFilled(false);
    //     setFocusPainted(false);
        
    //     // Добавляем эффект при наведении
    //     addMouseListener(new MouseAdapter() {
    //         @Override
    //         public void mouseEntered(MouseEvent e) {
    //             if (isEnabled()) {
    //                 setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
    //                 setLocation(getX(), getY() - 5);
    //             }
    //         }
            
    //         @Override
    //         public void mouseExited(MouseEvent e) {
    //             if (isEnabled()) {
    //                 setBorder(BorderFactory.createEmptyBorder());
    //                 if (!selected) {
    //                     setLocation(getX(), getY() + 5);
    //                 }
    //             }
    //         }
    //     });
    // }

    // Добавляем поддержку ActionListener
    public CardComponent(Card card, boolean faceUp) {
        this.card = card;
        this.faceUp = faceUp;
        this.selected = false;
        
        setPreferredSize(new Dimension(80, 120));
        setMinimumSize(new Dimension(70, 105));
        setMaximumSize(new Dimension(90, 135));
        setBorder(BorderFactory.createEmptyBorder());
        setContentAreaFilled(false);
        setFocusPainted(false);
        
        // Добавляем эффект при наведении и клике
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
                    setLocation(getX(), getY() - 5);
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (isEnabled()) {
                    setBorder(BorderFactory.createEmptyBorder());
                    if (!selected) {
                        setLocation(getX(), getY() + 5);
                    }
                }
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isEnabled()) {
                    fireActionPerformed();
                }
            }
        });
    }
    
    // Методы для работы с ActionListener
    @Override
    public void addActionListener(ActionListener listener) {
        actionListeners.add(listener);
    }
    
    @Override
    public void removeActionListener(ActionListener listener) {
        actionListeners.remove(listener);
    }
    
    private void fireActionPerformed() {
        ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "cardClicked");
        for (ActionListener listener : actionListeners) {
            listener.actionPerformed(event);
        }
    }
    
    public void setCard(Card card) {
        this.card = card;
        repaint();
    }
    
    public Card getCard() {
        return card;
    }
    
    public void setFaceUp(boolean faceUp) {
        this.faceUp = faceUp;
        repaint();
    }
    
    public void setSelected(boolean selected) {
        this.selected = selected;
        if (selected) {
            setBorder(BorderFactory.createLineBorder(Color.CYAN, 3));
            setLocation(getX(), getY() - 10);
        } else {
            setBorder(BorderFactory.createEmptyBorder());
            setLocation(getX(), getY() + 10);
        }
        repaint();
    }
    
    public void setAlpha(float alpha) {
        this.alpha = Math.max(0.0f, Math.min(1.0f, alpha));
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        
        // Включаем сглаживание
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Устанавливаем прозрачность
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        
        int width = getWidth();
        int height = getHeight();
        
        // Рисуем фон карты
        if (faceUp && card != null) {
            // Лицевая сторона
            drawFaceUpCard(g2d, width, height);
        } else {
            // Рубашка
            drawCardBack(g2d, width, height);
        }
        
        // Рисуем рамку если карта выбрана
        if (selected) {
            g2d.setColor(Color.CYAN);
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRoundRect(2, 2, width - 5, height - 5, 10, 10);
        }
        
        g2d.dispose();
    }
    
    private void drawFaceUpCard(Graphics2D g2d, int width, int height) {
        // Определяем цвет масти
        Color suitColor;
        switch (card.getSuit()) {
            case KEY:
                suitColor = KEY_COLOR;
                break;
            case BELL:
                suitColor = BELL_COLOR;
                break;
            case MOON:
                suitColor = MOON_COLOR;
                break;
            default:
                suitColor = Color.BLACK;
        }
        
        // Основной фон карты
        GradientPaint gradient = new GradientPaint(0, 0, Color.WHITE, 0, height, Color.LIGHT_GRAY);
        g2d.setPaint(gradient);
        g2d.fillRoundRect(0, 0, width - 1, height - 1, 15, 15);
        
        // Рамка карты
        g2d.setColor(suitColor);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(2, 2, width - 5, height - 5, 12, 12);
        
        // Рисуем достоинство
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 20));
        FontMetrics fm = g2d.getFontMetrics();
        
        String rankText = card.getRank().toString();
        int rankWidth = fm.stringWidth(rankText);
        
        // Верхний левый угол
        g2d.setColor(suitColor);
        g2d.drawString(rankText, 10, 25);
        
        // Нижний правый угол (перевернутый)
        g2d.translate(width - 10, height - 10);
        g2d.rotate(Math.PI);
        g2d.drawString(rankText, 0, 0);
        g2d.rotate(-Math.PI);
        g2d.translate(-(width - 10), -(height - 10));
        
        // Рисуем символ масти в центре
        g2d.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        String suitSymbol = getSuitSymbol(card.getSuit());
        int symbolWidth = g2d.getFontMetrics().stringWidth(suitSymbol);
        g2d.drawString(suitSymbol, (width - symbolWidth) / 2, height / 2 + 10);
        
        // Если карта нечетная (имеет эффект), добавляем специальный значок
        if (card.isOdd()) {
            g2d.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
            String effectIcon = getEffectIcon(card.getRank());
            int iconWidth = g2d.getFontMetrics().stringWidth(effectIcon);
            g2d.drawString(effectIcon, (width - iconWidth) / 2, height - 20);
        }
    }
    
    private void drawCardBack(Graphics2D g2d, int width, int height) {
        // Градиентный фон рубашки
        GradientPaint gradient = new GradientPaint(0, 0, BACK_COLOR, width, height, BACK_COLOR.darker());
        g2d.setPaint(gradient);
        g2d.fillRoundRect(0, 0, width - 1, height - 1, 15, 15);
        
        // Текстура рубашки
        g2d.setColor(new Color(255, 255, 255, 50));
        g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{2, 4}, 0));
        
        // Вертикальные линии
        for (int x = 10; x < width; x += 15) {
            g2d.drawLine(x, 5, x, height - 5);
        }
        
        // Горизонтальные линии
        for (int y = 10; y < height; y += 15) {
            g2d.drawLine(5, y, width - 5, y);
        }
        
        // Рамка
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(2, 2, width - 5, height - 5, 12, 12);
        
        // Логотип игры
        g2d.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        String logo = "🦊";
        FontMetrics fm = g2d.getFontMetrics();
        int logoWidth = fm.stringWidth(logo);
        g2d.drawString(logo, (width - logoWidth) / 2, height / 2 + 8);
        
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 10));
        String text = "Лиса на опушке";
        int textWidth = g2d.getFontMetrics().stringWidth(text);
        g2d.drawString(text, (width - textWidth) / 2, height / 2 + 30);
    }
    
    private String getSuitSymbol(Suit suit) {
        switch (suit) {
            case KEY: return "🗝️";
            case BELL: return "🔔";
            case MOON: return "🌙";
            default: return "?";
        }
    }
    
    private String getEffectIcon(Rank rank) {
        switch (rank) {
            case ONE: return "🦢";    // Лебедь
            case THREE: return "🦊";  // Лиса
            case FIVE: return "🪓";   // Дровосек
            case SEVEN: return "🛡️";  // Страж
            case NINE: return "🧙";   // Ведьма
            case ELEVEN: return "🌕"; // Лунатик
            default: return "";
        }
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (!enabled) {
            setAlpha(0.5f);
        } else {
            setAlpha(1.0f);
        }
    }
}
