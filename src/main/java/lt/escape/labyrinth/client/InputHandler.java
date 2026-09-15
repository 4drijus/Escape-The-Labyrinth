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
        int playerId = networkClient.getPlayerId();

        if (playerId == 1) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_A -> networkClient.sendCommand(PlayerCommand.LEFT);
                case KeyEvent.VK_D -> networkClient.sendCommand(PlayerCommand.RIGHT);
                case KeyEvent.VK_SPACE -> networkClient.sendCommand(PlayerCommand.JUMP);
            }

        } else if (playerId == 2) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT -> networkClient.sendCommand(PlayerCommand.LEFT);
                case KeyEvent.VK_RIGHT -> networkClient.sendCommand(PlayerCommand.RIGHT);
                case KeyEvent.VK_UP -> networkClient.sendCommand(PlayerCommand.JUMP);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int playerId = networkClient.getPlayerId();

        if (playerId == 1) {
            if (e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_D) {
                networkClient.sendCommand(PlayerCommand.STOP);
            }

        } else if (playerId == 2) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                networkClient.sendCommand(PlayerCommand.STOP);
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}