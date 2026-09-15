package lt.escape.labyrinth.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer {
    private static final int PORT = 5000;
    private static int nextPlayerId = 1;

    public static void main(String[] args) {
        System.out.println("Starting the server");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();

                if (nextPlayerId > 2) {
                    System.out.println("Can't connect: game is full");

                    clientSocket.close();
                    continue;
                }

                int playerId = nextPlayerId++;

                System.out.println("Player " + playerId + " connected: " + clientSocket.getInetAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket, playerId);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}