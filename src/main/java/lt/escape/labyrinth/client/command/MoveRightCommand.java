package lt.escape.labyrinth.client.command;

import lt.escape.labyrinth.client.NetworkClient;
import lt.escape.labyrinth.shared.PlayerCommand;

public class MoveRightCommand implements ICommand {
    private final NetworkClient networkClient;

    public MoveRightCommand(NetworkClient networkClient) {
        this.networkClient = networkClient;
    }

    @Override
    public void execute() {
        networkClient.sendCommand(PlayerCommand.RIGHT);
    }

    @Override
    public void undo() {
        networkClient.sendCommand(PlayerCommand.STOP);
    }
}
