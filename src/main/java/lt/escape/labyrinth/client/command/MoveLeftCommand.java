package lt.escape.labyrinth.client.command;

import lt.escape.labyrinth.client.NetworkClient;
import lt.escape.labyrinth.shared.PlayerCommand;

public class MoveLeftCommand implements ICommand {
    private final NetworkClient networkClient;

    public MoveLeftCommand(NetworkClient networkClient) {
        this.networkClient = networkClient;
    }

    @Override
    public void execute() {
        networkClient.sendCommand(PlayerCommand.LEFT);
    }

    @Override
    public void undo() {
        networkClient.sendCommand(PlayerCommand.STOP);
    }
}
