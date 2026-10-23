package lt.escape.labyrinth.client.command;

import lt.escape.labyrinth.client.NetworkClient;
import lt.escape.labyrinth.shared.PlayerCommand;

public class SaveCommand implements ICommand {
    private final NetworkClient networkClient;

    public SaveCommand(NetworkClient networkClient) {
        this.networkClient = networkClient;
    }

    @Override
    public void execute() {
        networkClient.sendCommand(PlayerCommand.SAVE);
    }

    @Override
    public void undo() {
        // Išsaugojimo atšaukti nereikia
    }
}
