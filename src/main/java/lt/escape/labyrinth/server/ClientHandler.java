package lt.escape.labyrinth.server;

import lt.escape.labyrinth.shared.GameState;
import lt.escape.labyrinth.shared.PlayerCommand;
import lt.escape.labyrinth.shared.PlayerState;
import lt.escape.labyrinth.shared.EnemyState;
import lt.escape.labyrinth.shared.BulletState;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

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

        StringBuilder message = new StringBuilder("STATE:");
        message.append(player1.getPlayerId()).append(",")
                .append(player1.getX()).append(",")
                .append(player1.getY()).append(",")
                .append(player2.getPlayerId()).append(",")
                .append(player2.getX()).append(",")
                .append(player2.getY());

        message.append("|ENEMIES:");
        List<EnemyState> enemies = gameState.getEnemies();
        for (int i = 0; i < enemies.size(); i++) {
            if (i > 0) {
                message.append(";");
            }
            EnemyState enemy = enemies.get(i);
            message.append(enemy.getType()).append(",")
                    .append(enemy.getX()).append(",")
                    .append(enemy.getY());
        }

        message.append("|BULLETS:");
        List<BulletState> bullets = gameState.getBullets();
        for (int i = 0; i < bullets.size(); i++) {
            if (i > 0) {
                message.append(";");
            }
            BulletState bullet = bullets.get(i);
            message.append(bullet.getX()).append(",").append(bullet.getY());
        }

        output.println(message);
    }
}