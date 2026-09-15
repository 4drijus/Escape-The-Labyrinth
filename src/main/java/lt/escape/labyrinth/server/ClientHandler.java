package lt.escape.labyrinth.server;

import lt.escape.labyrinth.shared.GameState;
import lt.escape.labyrinth.shared.PlayerCommand;
import lt.escape.labyrinth.shared.PlayerState;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final int playerId;
    private final GameState gameState;

    private PrintWriter output;
    private BufferedReader input;

    public ClientHandler(Socket socket, int playerId, GameState gameState) {
        this.socket = socket;
        this.playerId = playerId;
        this.gameState = gameState;
    }

    @Override
    public void run() {
        try {
            output = new PrintWriter(socket.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            output.println("PLAYER_ID:" + playerId);

            System.out.println("Player " + playerId + " handler started");

            String message;

            while ((message = input.readLine()) != null) {
                try {
                    PlayerCommand command = PlayerCommand.valueOf(message);
                    System.out.println("Player " + playerId + " sent command: " + command);

                    handleCommand(command);
                } catch (IllegalArgumentException e) {
                    System.out.println("Unknown command from Player " + playerId + ": " + message);
                }
            }
        } catch (IOException e) {
            System.out.println("Player " + playerId + " disconnected");
        }
    }

    private void handleCommand(PlayerCommand command) {
        PlayerState player = gameState.getPlayer(playerId);

        if (player == null) {
            return;
        }

        switch (command) {
            case LEFT:
                player.setVelocityX(-4);
                break;

            case RIGHT:
                player.setVelocityX(4);
                break;

            case STOP:
                player.setVelocityX(0);
                break;

            case JUMP:
                if (player.isOnGround()) {
                    player.setVelocityY(-8);
                    player.setOnGround(false);
                }
                break;
        }

        System.out.println("Player " + playerId + " velocityX = " + player.getVelocityX());
    }

    public void sendGameState() {
        if (output == null) {
            return;
        }

        PlayerState player1 = gameState.getPlayer1();
        PlayerState player2 = gameState.getPlayer2();

        String message =
                "STATE:" +
                        player1.getPlayerId() + "," +
                        player1.getX() + "," +
                        player1.getY() + "," +
                        player2.getPlayerId() + "," +
                        player2.getX() + "," +
                        player2.getY();

        output.println(message);
    }
}