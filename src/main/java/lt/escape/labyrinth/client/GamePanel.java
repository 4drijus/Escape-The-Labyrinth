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

        // Save players positions before moving
        double player1PreviousX = player1.getX();
        double player2PreviousX = player2.getX();

        player1.update();
        player2.update();

        checkPlayerCollision(player1PreviousX, player2PreviousX);

        repaint();
    }

    /**
     * Checks if the two players are overlapping.
     * If they collide, the player that moved into the other player is moved back
     * If both players move into each other both players are stopped
     */
    private void checkPlayerCollision(double player1PreviousX, double player2PreviousX) {
        double playerWidth = 40;
        double playerHeight = 40;

        double player1Left = player1.getX();
        double player1Right = player1.getX() + playerWidth;
        double player1Top = player1.getY();
        double player1Bottom = player1.getY() + playerHeight;

        double player2Left = player2.getX();
        double player2Right = player2.getX() + playerWidth;
        double player2Top = player2.getY();
        double player2Bottom = player2.getY() + playerHeight;

        // Check if the players are overlapping horizontally
        boolean overlappingX = player1Right > player2Left && player1Left < player2Right;

        // Check if the players are overlapping vertically
        boolean overlappingY = player1Bottom > player2Top && player1Top < player2Bottom;

        // Only handle collision if players overlap on both X and Y
        if (overlappingX && overlappingY) {

            // Check which direction each player moved
            boolean player1MovedLeft = player1.getX() < player1PreviousX;
            boolean player1MovedRight = player1.getX() > player1PreviousX;

            boolean player2MovedLeft = player2.getX() < player2PreviousX;
            boolean player2MovedRight = player2.getX() > player2PreviousX;

            // Player 1 moved left into Player 2
            if (player1MovedLeft && !player2MovedLeft) {

                // Put Player 1 directly to the right of Player 2
                player1.setX(player2Right);

                // Stop Player 1
                player1.stopHorizontalMovement();
            }

            // Player 1 moved right into Player 2
            else if (player1MovedRight && !player2MovedRight) {

                // Put Player 1 directly to the left of Player 2
                player1.setX(player2Left - playerWidth);

                // Stop Player 1
                player1.stopHorizontalMovement();
            }

            // Player 2 moved left into Player 1
            else if (player2MovedLeft && !player1MovedLeft) {

                // Put Player 2 directly to the right of Player 1
                player2.setX(player1Right);

                // Stop Player 2
                player2.stopHorizontalMovement();
            }

            // Player 2 moved right into Player 1
            else if (player2MovedRight && !player1MovedRight) {

                // Put Player 2 directly to the left of Player 1
                player2.setX(player1Left - playerWidth);

                // Stop Player 2
                player2.stopHorizontalMovement();
            }

            // Both players are moving toward each other
            else {

                // Restore both players to their previous positions
                player1.setX(player1PreviousX);
                player2.setX(player2PreviousX);

                // Stop both players
                player1.stopHorizontalMovement();
                player2.stopHorizontalMovement();
            }
        }
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