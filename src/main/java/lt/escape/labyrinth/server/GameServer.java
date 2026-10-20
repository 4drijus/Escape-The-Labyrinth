package lt.escape.labyrinth.server;

import lt.escape.labyrinth.server.interactive.*;
import lt.escape.labyrinth.server.level.*;
import lt.escape.labyrinth.shared.GameState;
import lt.escape.labyrinth.shared.PlayerState;
import lt.escape.labyrinth.shared.EnemyState;
import lt.escape.labyrinth.shared.BulletState;
import lt.escape.labyrinth.shared.WallState;
import lt.escape.labyrinth.shared.TileState;
import lt.escape.labyrinth.shared.TileType;
import lt.escape.labyrinth.shared.DoorState;
import lt.escape.labyrinth.shared.ButtonState;
import lt.escape.labyrinth.shared.PressurePlateState;
import lt.escape.labyrinth.shared.ExitState;
import lt.escape.labyrinth.server.enemy.Enemy;
import lt.escape.labyrinth.server.enemy.SpikyBush;
import lt.escape.labyrinth.server.enemy.Bullet;
import lt.escape.labyrinth.server.world.BlackPuddle;
import lt.escape.labyrinth.server.world.BluePuddle;
import lt.escape.labyrinth.server.world.Box;
import lt.escape.labyrinth.server.world.Bush;
import lt.escape.labyrinth.server.world.Lift;
import lt.escape.labyrinth.server.world.Potion;
import lt.escape.labyrinth.server.world.RedPuddle;
import lt.escape.labyrinth.server.world.Tile;
import lt.escape.labyrinth.server.world.Wall;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static lt.escape.labyrinth.server.GameConstants.BULLET_SIZE;
import static lt.escape.labyrinth.server.GameConstants.PLAYER_HEIGHT;
import static lt.escape.labyrinth.server.GameConstants.PLAYER_WIDTH;

public class GameServer {
    private static final int PORT = 5000;
    private static int nextPlayerId = 1;
    private static GameState gameState;

    private static ClientHandler player1Handler;
    private static ClientHandler player2Handler;

    private static final double DELTA_TIME_SECONDS = 16.0 / 1000.0;

    // ---- level sequence (Builder pattern: one Director, one concrete builder per level) ----
    private static final LevelDirector levelDirector = new LevelDirector();
    private static List<ILevelBuilder> levelBuilders;
    private static int currentLevelIndex = 0;

    // ---- state of the level that is currently loaded ----
    private static double levelHeight;
    private static double levelWidth;

    // Filled from the Level in loadLevel().
    private static final List<Enemy> enemies = new ArrayList<>();
    private static final List<Bullet> bullets = new ArrayList<>();

    private static Door door;
    private static Button button;
    private static PressurePlate pressurePlate;
    private static Exit exit;

    // Edge detection, so press()/trigger()/release() fire once per step on/off, not every tick.
    private static boolean buttonTouched;
    private static boolean plateOccupied;

    private static List<Wall> walls = new ArrayList<>();
    private static List<Tile> tiles = new ArrayList<>();
    private static List<Lift> lifts = new ArrayList<>();
    private static List<Box> boxes = new ArrayList<>();
    private static List<Potion> potions = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("Starting the server");

        gameState = GameState.getInstance();
        System.out.println("Game state created");

        InteractiveElementFactory elementFactory = new NormalElementFactory();
        // A level = a difficulty (builder) + a map (layout). Mix freely, e.g.
        // new HardLevelBuilder(elementFactory, LevelLayout.plains()) is a new level with no new class.
        levelBuilders = List.of(
                new EasyLevelBuilder(elementFactory, LevelLayout.plains()),
                new MediumLevelBuilder(elementFactory, LevelLayout.hill()),
                new HardLevelBuilder(elementFactory, LevelLayout.tower())
        );
        startLevel(currentLevelIndex);

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

    /** Asks the Director to assemble level number `index` with its concrete builder, then loads it. */
    private static void startLevel(int index) {
        System.out.println("Starting level " + (index + 1));
        Level level = levelDirector.construct(levelBuilders.get(index));
        loadLevel(level);
    }

    /** Called when both players stand in the exit: loads the next level and tells both clients. */
    private static void advanceToNextLevel() {
        System.out.println("Level " + (currentLevelIndex + 1) + " complete!");

        currentLevelIndex++;
        if (currentLevelIndex >= levelBuilders.size()) {
            System.out.println("All levels complete -- starting again from level 1");
            currentLevelIndex = 0;
        }

        startLevel(currentLevelIndex);
        broadcastLevelData(); // walls/tiles/door/... changed, clients need the new layout
    }

    private static void broadcastLevelData() {
        if (player1Handler != null) {
            player1Handler.sendLevelData();
        }
        if (player2Handler != null) {
            player2Handler.sendLevelData();
        }
    }

    private static void loadLevel(Level level) {
        levelWidth = level.getWidth();
        levelHeight = level.getHeight();

        enemies.clear();
        enemies.addAll(level.getEnemies());
        bullets.clear();

        door = level.getDoor();
        button = level.getButton();
        pressurePlate = level.getPressurePlate();
        exit = level.getExit();
        buttonTouched = false;
        plateOccupied = false;

        walls = level.getWalls();
        tiles = level.getTiles();
        lifts = level.getLifts();
        boxes = level.getBoxes();
        potions = level.getPotions();

        // Render snapshots that ClientHandler sends to the clients.
        List<WallState> wallStates = new ArrayList<>();
        for (Wall wall : walls) {
            wallStates.add(new WallState(wall.getPositionX(), wall.getPositionY(), wall.getWidth(), wall.getHeight()));
        }
        gameState.setWalls(wallStates);

        List<TileState> tileStates = new ArrayList<>();
        for (Tile tile : tiles) {
            tileStates.add(new TileState(tileTypeOf(tile), tile.getPositionX(), tile.getPositionY(), tile.getWidth(), tile.getHeight()));
        }
        gameState.setTiles(tileStates);

        gameState.setDoor(door == null ? null
                : new DoorState(door.getX(), door.getY(), door.getWidth(), door.getHeight(), door.isOpen()));
        gameState.setButton(button == null ? null
                : new ButtonState(button.getX(), button.getY(), button.getWidth(), button.getHeight()));
        gameState.setPressurePlate(pressurePlate == null ? null
                : new PressurePlateState(pressurePlate.getX(), pressurePlate.getY(), pressurePlate.getWidth(), pressurePlate.getHeight()));
        gameState.setExit(exit == null ? null
                : new ExitState(exit.getX(), exit.getY(), exit.getWidth(), exit.getHeight()));

        gameState.getEnemies().clear();
        initEnemyStates();
        gameState.setBullets(new ArrayList<>());

        // Put both players at this level's start positions, standing still.
        PlayerState player1 = gameState.getPlayer1();
        player1.setX(level.getPlayer1StartX());
        player1.setY(level.getPlayer1StartY());
        player1.setVelocityX(0);
        player1.setVelocityY(0);

        PlayerState player2 = gameState.getPlayer2();
        player2.setX(level.getPlayer2StartX());
        player2.setY(level.getPlayer2StartY());
        player2.setVelocityX(0);
        player2.setVelocityY(0);
    }

    private static TileType tileTypeOf(Tile tile) {
        if (tile instanceof BluePuddle) {
            return TileType.BLUE_PUDDLE;
        }
        if (tile instanceof RedPuddle) {
            return TileType.RED_PUDDLE;
        }
        if (tile instanceof BlackPuddle) {
            return TileType.BLACK_PUDDLE;
        }
        if (tile instanceof Bush) {
            return TileType.BUSH;
        }
        throw new IllegalArgumentException("Unknown tile type: " + tile.getClass());
    }

    /** Mirrors the loaded level's enemies into gameState's shared EnemyState snapshots. */
    private static void initEnemyStates() {
        for (Enemy enemy : enemies) {
            gameState.getEnemies().add(new EnemyState(enemy.getType(), enemy.getX(), enemy.getY()));
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
        List<PlayerState> players = List.of(player1, player2);

        updatePlayer(player1);
        updatePlayer(player2);

        updateEnemies(players);
        updateBullets(players);
        checkContactDamage(players);
        checkTileEffects(players);
        checkPotions(players);
        updateLifts();
        checkSwitches(players);

        if (door != null) {
            door.update(DELTA_TIME_SECONDS);
            if (gameState.getDoor() != null) {
                gameState.getDoor().setOpen(door.isOpen());
            }
        }

        // Last on purpose: this may replace the whole level.
        checkExit(players);

        broadcastGameState();
    }

    /** Both players inside the exit zone -> next level, automatically. */
    private static void checkExit(List<PlayerState> players) {
        if (exit == null || exit.isGameEnded()) {
            return;
        }
        if (exit.checkPlayerAmount(players)) {
            exit.endGame();
            advanceToNextLevel();
        }
    }

    /**
     * Touching the button presses it once; stepping on the pressure plate
     * triggers it, stepping off releases it. Nothing called these before,
     * so no door could ever be opened.
     */
    private static void checkSwitches(List<PlayerState> players) {
        if (button != null) {
            boolean touched = anyPlayerOverlaps(players, button);
            if (touched && !buttonTouched) {
                button.press();
            }
            buttonTouched = touched;
        }

        if (pressurePlate != null) {
            boolean occupied = anyPlayerOverlaps(players, pressurePlate);
            if (occupied && !plateOccupied) {
                pressurePlate.trigger();
            } else if (!occupied && plateOccupied) {
                pressurePlate.release();
            }
            plateOccupied = occupied;
        }
    }

    private static boolean anyPlayerOverlaps(List<PlayerState> players, InteractiveElement element) {
        for (PlayerState player : players) {
            if (rectsIntersect(player.getX(), player.getY(), PLAYER_WIDTH, PLAYER_HEIGHT,
                    element.getX(), element.getY(), element.getWidth(), element.getHeight())) {
                return true;
            }
        }
        return false;
    }

    /** Puddles and bushes -- whatever each Tile's onPlayerContact() does. */
    private static void checkTileEffects(List<PlayerState> players) {
        for (Tile tile : tiles) {
            for (PlayerState player : players) {
                if (tile.isTouching(player)) {
                    tile.onPlayerContact(player);
                }
            }
        }
    }

    private static void checkPotions(List<PlayerState> players) {
        for (Potion potion : potions) {
            for (PlayerState player : players) {
                if (potion.isTouching(player)) {
                    potion.collect(player);
                }
            }
        }
    }

    private static void updateLifts() {
        for (Lift lift : lifts) {
            lift.update(DELTA_TIME_SECONDS);
        }
    }


    private static void updateEnemies(List<PlayerState> players) {
        List<EnemyState> enemyStates = gameState.getEnemies();

        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            bullets.addAll(enemy.update(players, walls, DELTA_TIME_SECONDS));

            EnemyState state = enemyStates.get(i);
            state.setX(enemy.getX());
            state.setY(enemy.getY());
        }
    }

    private static void updateBullets(List<PlayerState> players) {
        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet bullet = it.next();
            bullet.update(DELTA_TIME_SECONDS);

            if (bullet.isOutOfBounds(levelWidth, levelHeight)) {
                it.remove();
                continue;
            }

            if (hitsWall(bullet)) {
                it.remove();
                continue;
            }

            for (PlayerState player : players) {
                if (bullet.hits(player)) {
                    bullet.onHit(player);
                    it.remove();
                    break;
                }
            }
        }

        List<BulletState> bulletStates = new ArrayList<>();
        for (Bullet bullet : bullets) {
            bulletStates.add(new BulletState(bullet.getType(), bullet.getX(), bullet.getY()));
        }
        gameState.setBullets(bulletStates);
    }

    // Matches the 8x8 sprite size from Assets. Bullet's own HIT_RADIUS
    // (used for hitting players) is private with no getter, so this is
    // an independent, server-side-only size just for wall collision.
    private static final double BULLET_SIZE = 8;    // TODO ar neperkelti

    private static boolean hitsWall(Bullet bullet) {
        for (Wall wall : walls) {
            if (wall.intersects(bullet.getX(), bullet.getY(), BULLET_SIZE, BULLET_SIZE)) {
                return true;
            }
        }
        return false;
    }

    /** Enemies like SpikyBush that damage on contact rather than by firing. */
    private static void checkContactDamage(List<PlayerState> players) {
        for (Enemy enemy : enemies) {
            if (enemy instanceof SpikyBush bush) {
                for (PlayerState player : players) {
                    if (bush.isTouching(player)) {
                        bush.damage(player);
                    }
                }
            }
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

    // Matches the 40x40 size the player sprites are drawn at in GamePanel.
    // PlayerState has no width/height field, so this is the best collision
    // box available without adding one.
    private static final double PLAYER_WIDTH = 40;  //TODO ar tikrai cia
    private static final double PLAYER_HEIGHT = 40;

    /**
     * No more hardcoded ground: players now land on and collide with
     * Wall objects exclusively. A level with no floor Wall means a
     * player who walks off one will fall indefinitely -- that's by
     * design now, not a bug, so make sure every level has a floor.
     */
    private static void updatePlayer(PlayerState player) {
        moveHorizontally(player);
        moveVertically(player);
    }

    private static void moveHorizontally(PlayerState player) {
        double newX = player.getX() + player.getVelocityX();
        newX = Math.clamp(newX, 0, levelWidth - PLAYER_WIDTH);  // needs java 21 version for older use newX = Math.max(0, Math.min(newX, levelWidth - PLAYER_WIDTH));

        for (Wall wall : walls) {
            if (wall.intersects(newX, player.getY(), PLAYER_WIDTH, PLAYER_HEIGHT)) {
                if (player.getVelocityX() > 0) {
                    newX = wall.getPositionX() - PLAYER_WIDTH; // moving right -> stop at wall's left edge
                } else if (player.getVelocityX() < 0) {
                    newX = wall.getPositionX() + wall.getWidth(); // moving left -> stop at wall's right edge
                }
            }
        }

        // A closed door blocks movement exactly like a Wall; an open one doesn't.
        if (doorBlocks(newX, player.getY())) {
            if (player.getVelocityX() > 0) {
                newX = door.getX() - PLAYER_WIDTH;
            } else if (player.getVelocityX() < 0) {
                newX = door.getX() + door.getWidth();
            }
        }

        player.setX(newX);
    }

    private static void moveVertically(PlayerState player) {
        player.setVelocityY(player.getVelocityY() + 0.5);
        double newY = player.getY() + player.getVelocityY();

        boolean onGround = false;
        for (Wall wall : walls) {
            if (wall.intersects(player.getX(), newY, PLAYER_WIDTH, PLAYER_HEIGHT)) {
                if (player.getVelocityY() > 0) {
                    // falling -> land on top of the wall
                    newY = wall.getPositionY() - PLAYER_HEIGHT;
                    player.setVelocityY(0);
                    onGround = true;
                } else if (player.getVelocityY() < 0) {
                    // jumping -> bump into the wall's underside
                    newY = wall.getPositionY() + wall.getHeight();
                    player.setVelocityY(0);
                }
            }
        }

        if (doorBlocks(player.getX(), newY)) {
            if (player.getVelocityY() > 0) {
                newY = door.getY() - PLAYER_HEIGHT;
                player.setVelocityY(0);
                onGround = true;
            } else if (player.getVelocityY() < 0) {
                newY = door.getY() + door.getHeight();
                player.setVelocityY(0);
            }
        }

        player.setY(newY);
        player.setOnGround(onGround);
    }

    /** True if the door is closed and a player box at (x, y) would overlap it. */
    private static boolean doorBlocks(double x, double y) {
        return door != null && !door.isOpen()
                && rectsIntersect(x, y, PLAYER_WIDTH, PLAYER_HEIGHT,
                door.getX(), door.getY(), door.getWidth(), door.getHeight());
    }

    /** Plain AABB overlap test, for interactive elements, which expose bounds but no intersects() of their own. */
    private static boolean rectsIntersect(double ax, double ay, double aw, double ah,
                                          double bx, double by, double bw, double bh) {
        return ax < bx + bw && ax + aw > bx && ay < by + bh && ay + ah > by;
    }
}