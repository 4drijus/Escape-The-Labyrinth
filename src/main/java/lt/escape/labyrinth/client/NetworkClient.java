package lt.escape.labyrinth.client;

import lt.escape.labyrinth.shared.PlayerCommand;
import lt.escape.labyrinth.shared.EnemyState;
import lt.escape.labyrinth.shared.EnemyType;
import lt.escape.labyrinth.shared.BulletState;

import java.io.IOException;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

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
            String[] sections = message.split("\\|");

            String playerData = sections[0].substring("STATE:".length());
            String[] values = playerData.split(",");

            double player1X = Double.parseDouble(values[1]);
            double player1Y = Double.parseDouble(values[2]);
            double player2X = Double.parseDouble(values[4]);
            double player2Y = Double.parseDouble(values[5]);

            gameState.update(player1X, player1Y, player2X, player2Y);
            gameState.updateEnemies(parseEnemies(sections));
            gameState.updateBullets(parseBullets(sections));
        } catch (Exception e) {
            System.out.println("Invalid game state: " + message);
        }
    }

    private List<EnemyState> parseEnemies(String[] sections) {
        List<EnemyState> enemies = new ArrayList<>();

        if (sections.length <= 1) {
            return enemies;
        }

        String data = sections[1].substring("ENEMIES:".length());
        if (data.isEmpty()) {
            return enemies;
        }

        for (String entry : data.split(";")) {
            String[] fields = entry.split(",");
            EnemyType type = EnemyType.valueOf(fields[0]);
            double x = Double.parseDouble(fields[1]);
            double y = Double.parseDouble(fields[2]);
            enemies.add(new EnemyState(type, x, y));
        }
        return enemies;
    }

    private List<BulletState> parseBullets(String[] sections) {
        List<BulletState> bullets = new ArrayList<>();

        if (sections.length <= 2) {
            return bullets;
        }

        String data = sections[2].substring("BULLETS:".length());
        if (data.isEmpty()) {
            return bullets;
        }

        for (String entry : data.split(";")) {
            String[] fields = entry.split(",");
            double x = Double.parseDouble(fields[0]);
            double y = Double.parseDouble(fields[1]);
            bullets.add(new BulletState(x, y));
        }
        return bullets;
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