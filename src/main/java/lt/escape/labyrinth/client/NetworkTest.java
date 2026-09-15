package lt.escape.labyrinth.client;

import lt.escape.labyrinth.shared.PlayerCommand;

import java.io.IOException;

public class NetworkTest {
    public static void main(String[] args) {
        NetworkClient networkClient = new NetworkClient();

        try {
            networkClient.connect("localhost", 5000);
            System.out.println("My player ID is: " + networkClient.getPlayerId());

            networkClient.sendCommand(PlayerCommand.RIGHT);
            Thread.sleep(1000);
            networkClient.sendCommand(PlayerCommand.STOP);
            Thread.sleep(500);

            ClientGameState state = networkClient.getGameState();

            System.out.println("Player 1: x=" + state.getPlayer1X() + ", y=" + state.getPlayer1Y());
            System.out.println("Player 2: x=" + state.getPlayer2X() + ", y=" + state.getPlayer2Y());

            networkClient.sendCommand(PlayerCommand.JUMP);
            Thread.sleep(500);

            System.out.println("After jump:");
            System.out.println("Player 1: x=" + state.getPlayer1X() + ", y=" + state.getPlayer1Y());
            System.out.println("Player 2: x=" + state.getPlayer2X() + ", y=" + state.getPlayer2Y());

            networkClient.disconnect();

        } catch (IOException e) {
            System.out.println("Could not connect to server");
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}