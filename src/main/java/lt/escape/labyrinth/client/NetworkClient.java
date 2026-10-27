package lt.escape.labyrinth.client;

import lt.escape.labyrinth.shared.PlayerCommand;
import lt.escape.labyrinth.shared.EnemyState;
import lt.escape.labyrinth.shared.EnemyType;
import lt.escape.labyrinth.shared.BulletState;
import lt.escape.labyrinth.shared.BulletType;
import lt.escape.labyrinth.shared.WallState;
import lt.escape.labyrinth.shared.TileState;
import lt.escape.labyrinth.shared.TileType;
import lt.escape.labyrinth.shared.DoorState;
import lt.escape.labyrinth.shared.ButtonState;
import lt.escape.labyrinth.shared.PressurePlateState;
import lt.escape.labyrinth.shared.ExitState;
import lt.escape.labyrinth.shared.LeverState;
import lt.escape.labyrinth.shared.LiftState;

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
                } else if (message.startsWith("LEVEL:")) {
                processLevelData(message);
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

            String healthData = findSection(sections, "HEALTH:");
            if (!healthData.isEmpty()) {
                String[] h = healthData.split(",");
                gameState.updateHealth(Integer.parseInt(h[0]), Integer.parseInt(h[1]));
            }

            String doorOpenData = findSection(sections, "DOOR_OPEN:");
            if (!doorOpenData.isEmpty()) {
                gameState.updateDoorOpen(Boolean.parseBoolean(doorOpenData));
            }

            gameState.updateLifts(parseLifts(sections));
            String leverOnData = findSection(sections, "LEVER_ON:");
            if (!leverOnData.isEmpty()) {
                gameState.updateLeverOn(Boolean.parseBoolean(leverOnData));
            }
        } catch (Exception e) {
            System.out.println("Invalid game state: " + message);
        }
    }

    /**
     * Sent once, right after PLAYER_ID -- static level geometry that
     * never changes after load, so it doesn't need to be re-parsed
     * every tick the way STATE: is.
     */
    private void processLevelData(String message) {
        try {
            String[] sections = message.split("\\|");

            List<WallState> walls = parseWalls(sections);
            List<TileState> tiles = parseTiles(sections);
            DoorState door = parseDoor(sections);
            ButtonState button = parseButton(sections);
            PressurePlateState pressurePlate = parsePressurePlate(sections);
            ExitState exit = parseExit(sections);

            gameState.updateLevelData(walls, tiles, door, button, pressurePlate, exit);
            gameState.updateLever(parseLever(sections));
        } catch (Exception e) {
            System.out.println("Invalid level data: " + message);
        }
    }

    private List<WallState> parseWalls(String[] sections) {
        List<WallState> walls = new ArrayList<>();
        String data = findSection(sections, "WALLS:");
        if (data.isEmpty()) {
            return walls;
        }
        for (String entry : data.split(";")) {
            String[] f = entry.split(",");
            walls.add(new WallState(
                    Double.parseDouble(f[0]), Double.parseDouble(f[1]),
                    Double.parseDouble(f[2]), Double.parseDouble(f[3])));
        }
        return walls;
    }

    private List<TileState> parseTiles(String[] sections) {
        List<TileState> tiles = new ArrayList<>();
        String data = findSection(sections, "TILES:");
        if (data.isEmpty()) {
            return tiles;
        }
        for (String entry : data.split(";")) {
            String[] f = entry.split(",");
            TileType type = TileType.valueOf(f[0]);
            tiles.add(new TileState(type,
                    Double.parseDouble(f[1]), Double.parseDouble(f[2]),
                    Double.parseDouble(f[3]), Double.parseDouble(f[4])));
        }
        return tiles;
    }

    private DoorState parseDoor(String[] sections) {
        String data = findSection(sections, "DOOR:");
        if (data.isEmpty()) {
            return null;
        }
        String[] f = data.split(",");
        // Starts closed; the per-tick DOOR_OPEN field updates this afterward.
        return new DoorState(
                Double.parseDouble(f[0]), Double.parseDouble(f[1]),
                Double.parseDouble(f[2]), Double.parseDouble(f[3]), false);
    }

    private ButtonState parseButton(String[] sections) {
        String data = findSection(sections, "BUTTON:");
        if (data.isEmpty()) {
            return null;
        }
        String[] f = data.split(",");
        return new ButtonState(
                Double.parseDouble(f[0]), Double.parseDouble(f[1]),
                Double.parseDouble(f[2]), Double.parseDouble(f[3]));
    }

    private PressurePlateState parsePressurePlate(String[] sections) {
        String data = findSection(sections, "PRESSUREPLATE:");
        if (data.isEmpty()) {
            return null;
        }
        String[] f = data.split(",");
        return new PressurePlateState(
                Double.parseDouble(f[0]), Double.parseDouble(f[1]),
                Double.parseDouble(f[2]), Double.parseDouble(f[3]));
    }

    private ExitState parseExit(String[] sections) {
        String data = findSection(sections, "EXIT:");
        if (data.isEmpty()) {
            return null;
        }
        String[] f = data.split(",");
        return new ExitState(
                Double.parseDouble(f[0]), Double.parseDouble(f[1]),
                Double.parseDouble(f[2]), Double.parseDouble(f[3]));
    }

    private LeverState parseLever(String[] sections) {
        String data = findSection(sections, "LEVER:");
        if (data.isEmpty()) {
            return null;
        }
        String[] f = data.split(",");
        // Starts off; the per-tick LEVER_ON field updates this afterward.
        return new LeverState(
                Double.parseDouble(f[0]), Double.parseDouble(f[1]),
                Double.parseDouble(f[2]), Double.parseDouble(f[3]), false);
    }

    private List<LiftState> parseLifts(String[] sections) {
        List<LiftState> lifts = new ArrayList<>();
        String data = findSection(sections, "LIFTS:");
        if (data.isEmpty()) {
            return lifts;
        }
        for (String entry : data.split(";")) {
            String[] f = entry.split(",");
            lifts.add(new LiftState(
                    Double.parseDouble(f[0]), Double.parseDouble(f[1]),
                    Double.parseDouble(f[2]), Double.parseDouble(f[3])));
        }
        return lifts;
    }

    /** Finds a "|"-delimited section by its prefix, instead of relying on a fixed index. */
    private String findSection(String[] sections, String prefix) {
        for (String section : sections) {
            if (section.startsWith(prefix)) {
                return section.substring(prefix.length());
            }
        }
        return "";
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
            BulletType type = BulletType.valueOf(fields[0]);
            double x = Double.parseDouble(fields[1]);
            double y = Double.parseDouble(fields[2]);
            bullets.add(new BulletState(type, x, y));
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