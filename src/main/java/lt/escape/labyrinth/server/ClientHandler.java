package lt.escape.labyrinth.server;

import lt.escape.labyrinth.server.engine.GameWorld;
import lt.escape.labyrinth.shared.GameState;
import lt.escape.labyrinth.shared.PlayerCommand;
import lt.escape.labyrinth.shared.PlayerState;
import lt.escape.labyrinth.shared.EnemyState;
import lt.escape.labyrinth.shared.BulletState;
import lt.escape.labyrinth.shared.WallState;
import lt.escape.labyrinth.shared.TileState;
import lt.escape.labyrinth.shared.DoorState;
import lt.escape.labyrinth.shared.ButtonState;
import lt.escape.labyrinth.shared.PressurePlateState;
import lt.escape.labyrinth.shared.ExitState;
import lt.escape.labyrinth.shared.LeverState;
import lt.escape.labyrinth.shared.LiftState;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final int playerId;
    private final GameWorld world;
    private final GameState gameState;

    private PrintWriter output;
    private BufferedReader input;

    public ClientHandler(Socket socket, int playerId, GameWorld world) {
        this.socket = socket;
        this.playerId = playerId;
        this.world = world;
        this.gameState = world.getState();
    }

    @Override
    public void run() {
        try {
            output = new PrintWriter(socket.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            output.println("PLAYER_ID:" + playerId);
            sendLevelData();

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
        world.applyCommand(playerId, command);
    }

    /**
     * Sent once, right after PLAYER_ID, instead of every tick like
     * sendGameState() -- walls/tiles/door/button/pressure plate/exit
     * never move, so there's no reason to resend their positions 60
     * times a second. This is what was missing entirely before: these
     * fields existed on GameState but nothing ever put them on the wire.
     */
    public void sendLevelData() {
        if (output == null) {
            return;
        }

        StringBuilder message = new StringBuilder("LEVEL:");

        message.append("|WALLS:");
        List<WallState> walls = gameState.getWalls();
        for (int i = 0; i < walls.size(); i++) {
            if (i > 0) {
                message.append(";");
            }
            WallState wall = walls.get(i);
            message.append(wall.getX()).append(",")
                    .append(wall.getY()).append(",")
                    .append(wall.getWidth()).append(",")
                    .append(wall.getHeight());
        }

        message.append("|TILES:");
        List<TileState> tiles = gameState.getTiles();
        for (int i = 0; i < tiles.size(); i++) {
            if (i > 0) {
                message.append(";");
            }
            TileState tile = tiles.get(i);
            message.append(tile.getType()).append(",")
                    .append(tile.getX()).append(",")
                    .append(tile.getY()).append(",")
                    .append(tile.getWidth()).append(",")
                    .append(tile.getHeight());
        }

        message.append("|DOOR:");
        DoorState door = gameState.getDoor();
        if (door != null) {
            message.append(door.getX()).append(",")
                    .append(door.getY()).append(",")
                    .append(door.getWidth()).append(",")
                    .append(door.getHeight());
        }

        message.append("|BUTTON:");
        ButtonState button = gameState.getButton();
        if (button != null) {
            message.append(button.getX()).append(",")
                    .append(button.getY()).append(",")
                    .append(button.getWidth()).append(",")
                    .append(button.getHeight());
        }

        message.append("|PRESSUREPLATE:");
        PressurePlateState plate = gameState.getPressurePlate();
        if (plate != null) {
            message.append(plate.getX()).append(",")
                    .append(plate.getY()).append(",")
                    .append(plate.getWidth()).append(",")
                    .append(plate.getHeight());
        }

        message.append("|EXIT:");
        ExitState exit = gameState.getExit();
        if (exit != null) {
            message.append(exit.getX()).append(",")
                    .append(exit.getY()).append(",")
                    .append(exit.getWidth()).append(",")
                    .append(exit.getHeight());
        }

        message.append("|LEVER:");
        LeverState lever = gameState.getLever();
        if (lever != null) {
            message.append(lever.getX()).append(",")
                    .append(lever.getY()).append(",")
                    .append(lever.getWidth()).append(",")
                    .append(lever.getHeight());
        }

        output.println(message);
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
            message.append(bullet.getType()).append(",")
                    .append(bullet.getX()).append(",")
                    .append(bullet.getY());
        }

        message.append("|DOOR_OPEN:");
        DoorState door = gameState.getDoor();
        message.append(door != null && door.isOpen());

        // Lifts move, so their position goes out every tick (appended last so
        // the fixed ENEMIES/BULLETS positions the client parses don't shift).
        message.append("|LIFTS:");
        List<LiftState> lifts = gameState.getLifts();
        for (int i = 0; i < lifts.size(); i++) {
            if (i > 0) {
                message.append(";");
            }
            LiftState lift = lifts.get(i);
            message.append(lift.getX()).append(",")
                    .append(lift.getY()).append(",")
                    .append(lift.getWidth()).append(",")
                    .append(lift.getHeight());
        }

        message.append("|LEVER_ON:");
        LeverState lever = gameState.getLever();
        message.append(lever != null && lever.isOn());

        output.println(message);
    }
}