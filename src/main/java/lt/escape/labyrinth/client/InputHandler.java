package lt.escape.labyrinth.client;

import lt.escape.labyrinth.client.command.ICommand;
import lt.escape.labyrinth.shared.PlayerCommand;
import lt.escape.labyrinth.client.command.*;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Stack;

public class InputHandler implements KeyListener {
    private final NetworkClient networkClient;
    private final Stack<ICommand> commandHistory = new Stack<>();

    public InputHandler(NetworkClient networkClient) {

        this.networkClient = networkClient;
    }

    private void executeCommand(ICommand command) {
        command.execute();
        commandHistory.push(command);
    }

    public void undoLastCommand() {
        if (!commandHistory.isEmpty()) {
            ICommand lastCommand = commandHistory.pop();
            lastCommand.undo();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

        switch (e.getKeyCode()) {
            case KeyEvent.VK_A -> executeCommand(new MoveLeftCommand(networkClient));
            case KeyEvent.VK_D -> executeCommand(new MoveRightCommand(networkClient));
            case KeyEvent.VK_SPACE -> executeCommand(new JumpCommand(networkClient));
            case KeyEvent.VK_Z -> undoLastCommand(); // atšaukia paskutinę komandą
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_D) {
            executeCommand(new StopCommand(networkClient));
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}