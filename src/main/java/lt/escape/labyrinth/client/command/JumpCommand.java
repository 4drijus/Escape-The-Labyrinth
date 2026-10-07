package lt.escape.labyrinth.client.command;

import lt.escape.labyrinth.client.NetworkClient;
import lt.escape.labyrinth.shared.PlayerCommand;

public class JumpCommand implements ICommand {
    private final NetworkClient networkClient;

    public JumpCommand(NetworkClient networkClient) {
        this.networkClient = networkClient;
    }

    @Override
    public void execute() {
        networkClient.sendCommand(PlayerCommand.JUMP);
    }

    @Override
    public void undo() {
        // Fiziškai šuolio atšaukti negalima
    }
}
