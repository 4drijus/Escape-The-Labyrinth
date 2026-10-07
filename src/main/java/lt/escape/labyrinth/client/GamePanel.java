package lt.escape.labyrinth.client;

import lt.escape.labyrinth.shared.BulletState;
import lt.escape.labyrinth.shared.EnemyState;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

public class GamePanel extends JPanel implements IObserver {
    private final NetworkClient networkClient;
    private final InputHandler inputHandler;
    private final Timer gameTimer;

    public GamePanel(NetworkClient networkClient) {
        this.networkClient = networkClient;
        setBackground(Color.DARK_GRAY);

        inputHandler = new InputHandler(networkClient);
        addKeyListener(inputHandler);
        setFocusable(true);

        // Užregistruojam GamePanel kaip IObserver prie ClientGameState būsenos
        if (networkClient.getGameState() != null) {
            networkClient.getGameState().registerObserver(this);
        }

        gameTimer = new Timer(16, e -> updateGame());
        gameTimer.start();

        requestFocusInWindow();
    }

    // IObserver interfeiso metodas: iškviečiamas automatiškai, kai ClientGameState informuoja apie pokyčius
    @Override
    public void update() {
        repaint();
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
        for (BulletState bullet : state.getBullets()) {
            Image sprite = Assets.getBulletSprite(bullet.getType());
            g.drawImage(sprite, (int) bullet.getX(), (int) bullet.getY(), this);
        }

        // Player 1
        g.drawImage(Assets.getPlayerBlueSprite(), (int) state.getPlayer1X(), (int) state.getPlayer1Y(), this);

        // Player 2
        g.drawImage(Assets.getPlayerRedSprite(), (int) state.getPlayer2X(), (int) state.getPlayer2Y(), this);
    }
}