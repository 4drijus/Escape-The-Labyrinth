package lt.escape.labyrinth.client;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Graphics;

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

        // Player 1
        g.setColor(Color.BLUE);
        g.fillRect((int) state.getPlayer1X(), (int) state.getPlayer1Y(), 40, 40);

        // Player 2
        g.setColor(Color.RED);
        g.fillRect((int) state.getPlayer2X(), (int) state.getPlayer2Y(), 40, 40);
    }
}