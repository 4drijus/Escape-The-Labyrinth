package lt.escape.labyrinth.client;

import lt.escape.labyrinth.shared.PlayerCommand;

import java.io.IOException;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class NetworkClient {
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private int playerId;

    private final ClientGameState gameState = new ClientGameState();

    public void connect(String host, int port) throws IOException {
        socket = new Socket(host, port);

        input = new BufferedReader(
                new InputStreamReader(
                        socket.getInputStream()
                )
        );

        output = new PrintWriter(
                socket.getOutputStream(),
                true);

        System.out.println("Connected to server");

        String message = input.readLine();

        if (message != null && message.startsWith("PLAYER_ID:")) {
            playerId = Integer.parseInt(
                    message.substring("PLAYER_ID:".length())
            );

            System.out.println("Player " + playerId);
        }

        Thread receiverThread = new Thread(this::receiveMessages);
        receiverThread.start();
    }

    private void receiveMessages() {
        try {
            String message;

            while ((message = input.readLine()) != null) {
                if (message.startsWith("STATE:")) {
                    processGameState(message);
                }
            }
        } catch (IOException e) {
            System.out.println("Disconnected from server");
        }
    }

    private void processGameState(String message) {
        try {
            String data = message.substring("STATE:".length());
            String[] values = data.split(",");

            double player1X = Double.parseDouble(values[1]);
            double player1Y = Double.parseDouble(values[2]);
            double player2X = Double.parseDouble(values[4]);
            double player2Y = Double.parseDouble(values[5]);

            gameState.update(player1X, player1Y, player2X, player2Y);
        } catch (Exception e) {
            System.out.println("Invalid game state: " + message);
        }
    }

    public int getPlayerId() {
        return playerId;
    }

    public ClientGameState getGameState() {
        return gameState;
    }

    public void sendCommand(PlayerCommand command) {
        if (output != null) {
            output.println(command.name());
        }
    }


    public void disconnect() {
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}