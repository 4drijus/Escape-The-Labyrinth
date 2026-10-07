package lt.escape.labyrinth.client.command;

import lt.escape.labyrinth.client.NetworkClient;
import lt.escape.labyrinth.shared.PlayerCommand;

public class StopCommand implements ICommand {
    private final NetworkClient networkClient;

    public StopCommand (NetworkClient networkClient) {
        this.networkClient = networkClient;
    }

    @Override
    public void execute() {
        networkClient.sendCommand(PlayerCommand.STOP);
    }

    @Override
    public void undo() {
        // Sustabdymo atšaukti nereikia
    }
}
