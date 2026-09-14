package lt.escape.labyrinth.client;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.Timer;

public class GamePanel extends JPanel {
    private final Player player;
    private final InputHandler inputHandler;
    private final Timer gameTimer;

    public GamePanel() {
        setBackground(Color.DARK_GRAY);

        player = new Player(500,480);
        inputHandler = new InputHandler();

        addKeyListener(inputHandler);
        setFocusable(true);

        gameTimer = new Timer(16, e -> updateGame());
        gameTimer.start();

        requestFocusInWindow();
    }

    public void updateGame() {
        if (inputHandler.isLeft()) {
            player.moveLeft();
        } else if (inputHandler.isRight()) {
            player.moveRight();
        } else {
            player.stopHorizontalMovement();
        }

        if (inputHandler.consumeJumpPressed()) {
            player.jump();
        }

        player.update();

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        //ground
        g.setColor(Color.GREEN);
        g.fillRect(0, 520, getWidth(), 80);

        //player
        g.setColor(Color.BLUE);
        g.fillRect(
                (int) player.getX(),
                (int) player.getY(),
                40,
                40
        );
    }
}