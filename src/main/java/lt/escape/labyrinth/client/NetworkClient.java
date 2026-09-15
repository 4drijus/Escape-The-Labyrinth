package lt.escape.labyrinth.client;

import java.io.IOException;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class NetworkClient {
    private Socket socket;
    private BufferedReader input;
    private int playerId;

    public void connect(String host, int port) throws IOException {
        socket = new Socket(host, port);

        input = new BufferedReader(
                new InputStreamReader(
                        socket.getInputStream()
                )
        );
        System.out.println("Connected to server");

        String message = input.readLine();

        if (message != null && message.startsWith("PLAYER_ID:")) {
            playerId = Integer.parseInt(
                    message.substring("PLAYER_ID:".length())
            );

            System.out.println("Player " + playerId);
        }
    }

    public int getPlayerId() {
        return playerId;
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