package lt.escape.labyrinth.client;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class InputHandler implements KeyListener {
    private boolean player1Left;
    private boolean player1Right;
    private boolean player1jJump;

    private boolean player2Left;
    private boolean player2Right;
    private boolean player2jJump;

    /**
     * Called when a keyboard key is pressed
     * Checks which key it was and updates movement variables
     */
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A -> player1Left = true;
            case KeyEvent.VK_D -> player1Right = true;
            case KeyEvent.VK_SPACE -> player1jJump = true;

            case KeyEvent.VK_LEFT -> player2Left = true;
            case KeyEvent.VK_RIGHT -> player2Right = true;
            case KeyEvent.VK_UP -> player2jJump = true;
        }
    }

    /**
     * Called when a keyboard key is released
     * Stops movement to left and right when key is released
     */
    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A -> player1Left = false;
            case KeyEvent.VK_D -> player1Right = false;

            case KeyEvent.VK_LEFT -> player2Left = false;
            case KeyEvent.VK_RIGHT -> player2Right = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    public boolean isPlayer1Left() {
        return player1Left;
    }

    public boolean isPlayer2Left() {
        return player2Left;
    }

    public boolean isPlayer1Right() {
        return player1Right;
    }

    public boolean isPlayer2Right() {
        return player2Right;
    }

    /**
     * Checks if the space key is pressed
     * Resets the value after detecting a jump so the same press is not added multiple times
     * @return true if space was pressed, otherwise false
     */
    public boolean consumePlayer1Jump() {
        if (player1jJump) {
            player1jJump = false;
            return true;
        }
        return false;
    }

    public boolean consumePlayer2Jump() {
        if (player2jJump) {
            player2jJump = false;
            return true;
        }
        return false;
    }


}
