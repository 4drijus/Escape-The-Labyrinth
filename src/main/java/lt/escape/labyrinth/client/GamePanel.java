package lt.escape.labyrinth.client;

import lt.escape.labyrinth.shared.BulletState;
import lt.escape.labyrinth.shared.EnemyState;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

public class GamePanel extends JPanel {
    private final NetworkClient networkClient;
    private final InputHandler inputHandler;
    private final Timer gameTimer;

    public GamePanel(NetworkClient networkClient) {
        this.networkClient = networkClient;
        setBackground(Color.DARK_GRAY);

        inputHandler = new InputHandler(networkClient);
        addKeyListener(inputHandler);
        setFocusable(true);

        gameTimer = new Timer(16, e -> updateGame());
        gameTimer.start();

        requestFocusInWindow();
    }

    private void updateGame() {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ClientGameState state = networkClient.getGameState();

        // Ground
        g.setColor(Color.GREEN);
        g.fillRect(0, 520, getWidth(), 80);

        // Enemies
        for (EnemyState enemy : state.getEnemies()) {
            Image sprite = Assets.getEnemySprite(enemy.getType());
            g.drawImage(sprite, (int) enemy.getX(), (int) enemy.getY(), this);
        }

        // Bullets
        Image bulletSprite = Assets.getBulletSprite();
        for (BulletState bullet : state.getBullets()) {
            g.drawImage(bulletSprite, (int) bullet.getX(), (int) bullet.getY(), this);
        }

        // Player 1
        g.drawImage(Assets.getPlayerBlueSprite(), (int) state.getPlayer1X(), (int) state.getPlayer1Y(), this);

        // Player 2
        g.drawImage(Assets.getPlayerRedSprite(), (int) state.getPlayer2X(), (int) state.getPlayer2Y(), this);
    }
}