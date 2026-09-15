package lt.escape.labyrinth.server;

import lt.escape.labyrinth.shared.GameState;
import lt.escape.labyrinth.shared.PlayerState;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer {
    private static final int PORT = 5000;
    private static int nextPlayerId = 1;
    private static GameState gameState;

    private static ClientHandler player1Handler;
    private static ClientHandler player2Handler;

    public static void main(String[] args) {
        System.out.println("Starting the server");

        gameState = new GameState();
        System.out.println("Game state created");

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

                ClientHandler clientHandler = new ClientHandler(clientSocket, playerId, gameState);

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
        PlayerState player1 = gameState.getPlayer1();
        PlayerState player2 = gameState.getPlayer2();

        updatePlayer(player1);
        updatePlayer(player2);

        broadcastGameState();
    }

    private static void broadcastGameState() {
        if (player1Handler != null) {
            player1Handler.sendGameState();
        }
        if (player2Handler != null) {
            player2Handler.sendGameState();
        }
    }

    private static void updatePlayer(PlayerState player) {
        player.setX(player.getX() + player.getVelocityX());

        if (player.getX() < 0) {
            player.setX(0);
        }

        if (player.getX() >960) {
            player.setX(960);
        }

        player.setY(player.getY() + player.getVelocityY());

        player.setVelocityY(player.getVelocityY() + 0.5);

        if (player.getY() >=480) {
            player.setY(480);
            player.setVelocityY(0);
            player.setOnGround(true);
        }
    }
}