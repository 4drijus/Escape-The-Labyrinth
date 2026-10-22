package lt.escape.labyrinth.server;

import lt.escape.labyrinth.server.engine.GameWorld;
import lt.escape.labyrinth.server.interactive.*;
import lt.escape.labyrinth.server.level.*;
import lt.escape.labyrinth.shared.GameState;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer {
    private static final int PORT = 5000;
    private static int nextPlayerId = 1;
    private static GameWorld world;

    private static ClientHandler player1Handler;
    private static ClientHandler player2Handler;

    public static void main(String[] args) {
        System.out.println("Starting the server");

        // Swap NormalElementFactory for TimedElementFactory to get timed doors/buttons in every level.
        InteractiveElementFactory elementFactory = new NormalElementFactory();

        world = new GameWorld(GameState.getInstance(), elementFactory);
        world.start();

        Thread gameThread = new Thread(GameServer::gameLoop);

        gameThread.start();

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

                ClientHandler clientHandler = new ClientHandler(clientSocket, playerId, world);

                if (playerId == 1) {
                    player1Handler = clientHandler;
                }
                if (playerId == 2) {
                    player2Handler = clientHandler;
                }

                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void gameLoop() {
        final long frameTime = 16_000_000;

        while (true) {
            long startTime = System.nanoTime();
            updateGame();
            long elapsedTime = System.nanoTime() - startTime;

            long sleepTime = frameTime - elapsedTime;
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime / 1_000_000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    private static void updateGame() {
        boolean levelChanged = world.update();

        if (levelChanged) {
            broadcastLevelData(); // walls/tiles/door/... changed, clients need the new layout first
        }
        broadcastGameState();
    }

    private static void broadcastLevelData() {
        if (player1Handler != null) {
            player1Handler.sendLevelData();
        }
        if (player2Handler != null) {
            player2Handler.sendLevelData();
        }
    }

    private static void broadcastGameState() {
        if (player1Handler != null) {
            player1Handler.sendGameState();
        }
        if (player2Handler != null) {
            player2Handler.sendGameState();
        }
    }
}