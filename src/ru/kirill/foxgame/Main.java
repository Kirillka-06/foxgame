package ru.kirill.foxgame;

import ru.kirill.foxgame.logic.FoxGame;
import ru.kirill.foxgame.view.GameFrame;
import ru.kirill.foxgame.controller.GameController;
import javax.swing.*;

/**
 * Главный класс приложения, точка входа.
 */
public class Main {
    public static void main(String[] args) {
        // Устанавливаем Look and Feel для более современного вида
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Используем стандартный Look and Feel
        }
        
        // Запускаем приложение в потоке событий Swing
        SwingUtilities.invokeLater(() -> {
            try {
                // Создаем игру и интерфейс
                FoxGame game = new FoxGame();
                GameFrame frame = new GameFrame(game);
                
                // Создаем контроллер
                GameController controller = new GameController(game, frame);
                
                // Делаем окно видимым
                frame.setVisible(true);
                
                // Добавляем слушатель закрытия окна
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                        controller.dispose();
                    }
                });
                
                // Показываем приветственное сообщение
                JOptionPane.showMessageDialog(frame,
                    "Добро пожаловать в игру 'Лисица на опушке'!\n\n" +
                    "Правила:\n" +
                    "1. Вы играете против компьютера\n" +
                    "2. Кликайте по картам, чтобы сделать ход\n" +
                    "3. Наберите 21 очко раньше соперника\n\n" +
                    "Удачи в игре!",
                    "Добро пожаловать!",
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Ошибка при запуске игры: " + e.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
