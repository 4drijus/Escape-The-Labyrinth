package lt.escape.labyrinth.client;

import lt.escape.labyrinth.shared.PlayerCommand;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputHandler implements KeyListener {
    private final NetworkClient networkClient;

    public InputHandler(NetworkClient networkClient) {
        this.networkClient = networkClient;
    }

    @Override
    public void keyPressed(KeyEvent e) {

        switch (e.getKeyCode()) {
            case KeyEvent.VK_A -> networkClient.sendCommand(PlayerCommand.LEFT);
            case KeyEvent.VK_D -> networkClient.sendCommand(PlayerCommand.RIGHT);
            case KeyEvent.VK_SPACE -> networkClient.sendCommand(PlayerCommand.JUMP);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int playerId = networkClient.getPlayerId();

        if (e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_D) {
            networkClient.sendCommand(PlayerCommand.STOP);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}