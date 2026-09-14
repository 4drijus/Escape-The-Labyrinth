package lt.escape.labyrinth.client;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class InputHandler implements KeyListener {
    private boolean left;
    private boolean right;

    private boolean jumpPressed;


    /**
     * Called when a keyboard key is pressed
     * Checks which key it was and updates movement variables
     */
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A -> left = true;
            case KeyEvent.VK_D -> right = true;
            case KeyEvent.VK_SPACE -> jumpPressed = true;
        }
    }

    /**
     * Called when a keyboard key is released
     * Stops movement to left and right when key is released
     */
    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A -> left = false;
            case KeyEvent.VK_D -> right = false;
        }
    }

    public boolean isLeft() {
        return left;
    }

    public boolean isRight() {
        return right;
    }

    /**
     * Checks if the space key is pressed
     * Resets the value after detecting a jump so the same press is not added multiple times
     * @return true if space was pressed, otherwise false
     */
    public boolean consumeJumpPressed() {
        if (jumpPressed) {
            jumpPressed = false;
            return true;
        }
        return false;
    }
}
