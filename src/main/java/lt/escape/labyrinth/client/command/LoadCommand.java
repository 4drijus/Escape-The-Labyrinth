package lt.escape.labyrinth.client.command;

import lt.escape.labyrinth.client.NetworkClient;
import lt.escape.labyrinth.shared.PlayerCommand;

public class LoadCommand implements ICommand {
    private final NetworkClient networkClient;

    public LoadCommand(NetworkClient networkClient) {
        this.networkClient = networkClient;
    }

    @Override
    public void execute() {
        networkClient.sendCommand(PlayerCommand.LOAD);
    }

    @Override
    public void undo() {
        // Įkėlimo atšaukti negalima
    }
}
