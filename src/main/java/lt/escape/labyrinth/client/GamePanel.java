package lt.escape.labyrinth.client;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.Timer;

public class GamePanel extends JPanel {
    private final Player player1;
    private final Player player2;
    private final InputHandler inputHandler;
    private final Timer gameTimer;

    public GamePanel() {
        setBackground(Color.DARK_GRAY);

        player1 = new Player(1, 500,480);
        player2 = new Player(2, 100, 480);

        inputHandler = new InputHandler();

        addKeyListener(inputHandler);
        setFocusable(true);

        gameTimer = new Timer(16, e -> updateGame());
        gameTimer.start();

        requestFocusInWindow();
    }

    public void updateGame() {

        // Player 1 input
        if (inputHandler.isPlayer1Left()) {
            player1.moveLeft();
        } else if (inputHandler.isPlayer1Right()) {
            player1.moveRight();
        } else {
            player1.stopHorizontalMovement();
        }

        if (inputHandler.consumePlayer1Jump()) {
            player1.jump();
        }

        // Player 2 input
        if (inputHandler.isPlayer2Left()) {
            player2.moveLeft();
        } else if (inputHandler.isPlayer2Right()) {
            player2.moveRight();
        } else {
            player2.stopHorizontalMovement();
        }

        if (inputHandler.consumePlayer2Jump()) {
            player2.jump();
        }

        player1.update();
        player2.update();

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        //ground
        g.setColor(Color.GREEN);
        g.fillRect(0, 520, getWidth(), 80);

        // Player 1
        g.setColor(Color.BLUE);
        g.fillRect(
                (int) player1.getX(),
                (int) player1.getY(),
                40,
                40
        );

        // Player 2
        g.setColor(Color.RED);
        g.fillRect(
                (int) player2.getX(),
                (int) player2.getY(),
                40,
                40
        );
    }
}