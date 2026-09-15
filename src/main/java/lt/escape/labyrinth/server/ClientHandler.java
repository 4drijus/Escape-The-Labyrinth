package lt.escape.labyrinth.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final int playerId;

    private PrintWriter output;

    public ClientHandler(Socket socket, int playerId) {
        this.socket = socket;
        this.playerId = playerId;
    }

    @Override
    public void run() {
        try {
            output = new PrintWriter(socket.getOutputStream(), true);

            output.println("PLAYER_ID:" + playerId);

            System.out.println("Player " + playerId + " handler started");

            while (!socket.isClosed()) {
                Thread.sleep(100);
            }
        } catch (IOException e) {
            System.out.println("Player " + playerId + " disconnected");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}