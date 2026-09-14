package lt.escape.labyrinth.client;

import javax.swing.SwingUtilities;

public class ClientApplication {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameWindow window = new GameWindow();
            window.setVisible(true);

            GamePanel gamePanel = (GamePanel) window.getContentPane().getComponent(0);

            Thread gameThread = new Thread(() -> {
                final long grameTime = 1_000_000_000L / 60;

                while (true) {
                    long startTime = System.nanoTime();
                    gamePanel.updateGame();
                    long elapsedTime = System.nanoTime() - startTime;
                    long sleepTime = grameTime - elapsedTime;

                    if (sleepTime > 0) {
                        try {
                            Thread.sleep(sleepTime / 1_000_000);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
            });

            gameThread.start();
        });
    }
}