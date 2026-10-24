package lt.escape.labyrinth.client;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Stack;

import lt.escape.labyrinth.client.command.ICommand;
import lt.escape.labyrinth.client.command.JumpCommand;
import lt.escape.labyrinth.client.command.LoadCommand;
import lt.escape.labyrinth.client.command.MoveLeftCommand;
import lt.escape.labyrinth.client.command.MoveRightCommand;
import lt.escape.labyrinth.client.command.SaveCommand;
import lt.escape.labyrinth.client.command.StopCommand;

public class InputHandler implements KeyListener {
    private final NetworkClient networkClient;
    private final Stack<ICommand> commandHistory = new Stack<>();
    private final Runnable editNameAction;

    public InputHandler(
            NetworkClient networkClient,
            Runnable editNameAction
    ) {
        this.networkClient = networkClient;
        this.editNameAction = editNameAction;
       // System.out.println("NEW InputHandler loaded");
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
            case KeyEvent.VK_A ->
                executeCommand(new MoveLeftCommand(networkClient));
            case KeyEvent.VK_D ->
                executeCommand(new MoveRightCommand(networkClient));
            case KeyEvent.VK_SPACE ->
                executeCommand(new JumpCommand(networkClient));
            case KeyEvent.VK_Z ->
                undoLastCommand(); // atšaukia paskutinę komandą
            case KeyEvent.VK_F5 ->
                executeCommand(new SaveCommand(networkClient));
            case KeyEvent.VK_F9 ->
                executeCommand(new LoadCommand(networkClient));
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        System.out.println("KEY RELEASED: " + e.getKeyCode());
        if (e.getKeyCode() == KeyEvent.VK_A
                || e.getKeyCode() == KeyEvent.VK_D) {
            executeCommand(new StopCommand(networkClient));
        }

        if (e.getKeyCode() == KeyEvent.VK_N) {
            editNameAction.run();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
