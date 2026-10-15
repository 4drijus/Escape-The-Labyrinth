package lt.escape.labyrinth.server;

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
import lt.escape.labyrinth.server.interactive.Door;
import lt.escape.labyrinth.server.interactive.Button;
import lt.escape.labyrinth.server.interactive.PressurePlate;
import lt.escape.labyrinth.server.interactive.NormalElementFactory;
import lt.escape.labyrinth.server.interactive.Exit;
import lt.escape.labyrinth.server.level.Level;
import lt.escape.labyrinth.server.level.LevelDirector;
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

public class GameServer {
    private static final int PORT = 5000;
    private static int nextPlayerId = 1;
    private static GameState gameState;

    private static ClientHandler player1Handler;
    private static ClientHandler player2Handler;

    // Set from the Level in loadLevel(), before the game loop thread starts.
    // (updatePlayer() below still hardcodes 960 / 480 for the player bounds.)
    private static double levelWidth;
    private static double levelHeight;

    private static final double DELTA_TIME_SECONDS = 16.0 / 1000.0;

    // Filled from the Level in loadLevel().
    private static final List<Enemy> enemies = new ArrayList<>();

    private static final List<Bullet> bullets = new ArrayList<>();

    private static Door door;
    private static Level.Rect doorRect; // position only; Door itself carries no x/y
    private static Button button;
    private static PressurePlate pressurePlate;
    private static Exit exit; // nullable -- not every level configures one yet

    private static List<Wall> walls = new ArrayList<>();
    private static List<Tile> tiles = new ArrayList<>();
    private static List<Lift> lifts = new ArrayList<>();
    private static List<Box> boxes = new ArrayList<>();
    private static List<Potion> potions = new ArrayList<>();

    // Prototype: the Level LevelDirector built, kept untouched. The game
    // always plays a copy of it, so restartLevel() can take a fresh one.
    private static Level levelTemplate;

    public static void main(String[] args) {
        System.out.println("Starting the server");

        gameState = GameState.getInstance();
        System.out.println("Game state created");

        //levelTemplate = LevelDirector.buildLevelOne(new NormalElementFactory());
        //levelTemplate = LevelDirector.buildLevelTwo(new NormalElementFactory());
        //levelTemplate = LevelDirector.buildLevelThree(new NormalElementFactory());
        levelTemplate = LevelDirector.buildLevelFour(new NormalElementFactory());   //TODO pasiziuret kaip pakeist lygiu ir pan.
        loadLevel(levelTemplate.copy());

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

    private static void loadLevel(Level level) {
        levelWidth = level.getWidth();
        levelHeight = level.getHeight();

        // Drop what the previous copy of the level left behind, so
        // loadLevel() can run again from restartLevel().
        enemies.clear();
        bullets.clear();
        gameState.getEnemies().clear();
        gameState.setBullets(new ArrayList<>());

        enemies.addAll(level.getEnemies());
        door = level.getDoor();
        button = level.getButton();
        pressurePlate = level.getPressurePlate();
        exit = level.getExit();

        walls = level.getWalls();
        tiles = level.getTiles();
        lifts = level.getLifts();
        boxes = level.getBoxes();
        potions = level.getPotions();

        // Build the one-time render snapshots GameState exposes to the
        // client (via ClientHandler's "LEVEL:" message)
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

        doorRect = level.getDoorRect();
        if (doorRect != null) {
            gameState.setDoor(new DoorState(doorRect.x, doorRect.y, doorRect.width, doorRect.height, door.isOpen()));
        }

        Level.Rect buttonRect = level.getButtonRect();
        if (buttonRect != null) {
            gameState.setButton(new ButtonState(buttonRect.x, buttonRect.y, buttonRect.width, buttonRect.height));
        }

        Level.Rect pressurePlateRect = level.getPressurePlateRect();
        if (pressurePlateRect != null) {
            gameState.setPressurePlate(new PressurePlateState(
                    pressurePlateRect.x, pressurePlateRect.y, pressurePlateRect.width, pressurePlateRect.height));
        }

        if (exit != null) {
            gameState.setExit(new ExitState(exit.getPositionX(), exit.getPositionY(), exit.getWidth(), exit.getHeight()));
        }

        PlayerState player1 = gameState.getPlayer1();
        player1.respawn(level.getPlayer1StartX(), level.getPlayer1StartY());

        PlayerState player2 = gameState.getPlayer2();
        player2.respawn(level.getPlayer2StartX(), level.getPlayer2StartY());

        initEnemyStates();
    }

    /**
     * Prototype in use: a fresh copy of the untouched template instead of
     * running LevelDirector again. Called from updateGame(), on the game
     * loop thread, so nothing is iterating the lists loadLevel() clears.
     */
    private static void restartLevel() {
        System.out.println("A player died -- restarting the level");
        loadLevel(levelTemplate.copy());
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
        checkExit(players);
        door.update(DELTA_TIME_SECONDS);//keeping here for now
        if (gameState.getDoor() != null) {
            gameState.getDoor().setOpen(door.isOpen());
        }
        if (!player1.isAlive() || !player2.isAlive()) {
            restartLevel();
        }
        broadcastGameState();
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

    /** No-op until a level actually configures an Exit with withExit(). */
    private static void checkExit(List<PlayerState> players) {
        if (exit == null || exit.isGameEnded()) {
            return;
        }
        if (exit.checkPlayerAmount(players)) {
            exit.endGame();
            System.out.println("Both players reached the exit -- level complete");
        }
    }

    /** Mirrors the (fixed, hardcoded-for-now) enemies list into gameState's shared EnemyState snapshots, one time, at startup. */
    private static void initEnemyStates() {
        for (Enemy enemy : enemies) {
            gameState.getEnemies().add(new EnemyState(enemy.getType(), enemy.getX(), enemy.getY()));
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
    private static final double BULLET_SIZE = 8;    // for now ig

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
    private static final double PLAYER_WIDTH = 40;
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
        if (doorRect != null && !door.isOpen()
                && rectsIntersect(newX, player.getY(), PLAYER_WIDTH, PLAYER_HEIGHT,
                doorRect.x, doorRect.y, doorRect.width, doorRect.height)) {
            if (player.getVelocityX() > 0) {
                newX = doorRect.x - PLAYER_WIDTH;
            } else if (player.getVelocityX() < 0) {
                newX = doorRect.x + doorRect.width;
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

        if (doorRect != null && !door.isOpen()
                && rectsIntersect(player.getX(), newY, PLAYER_WIDTH, PLAYER_HEIGHT,
                doorRect.x, doorRect.y, doorRect.width, doorRect.height)) {
            if (player.getVelocityY() > 0) {
                newY = doorRect.y - PLAYER_HEIGHT;
                player.setVelocityY(0);
                onGround = true;
            } else if (player.getVelocityY() < 0) {
                newY = doorRect.y + doorRect.height;
                player.setVelocityY(0);
            }
        }

        player.setY(newY);
        player.setOnGround(onGround);
    }

    /** Plain AABB overlap test -- used for the door, which (unlike Wall) has no intersects() of its own. */
    private static boolean rectsIntersect(double ax, double ay, double aw, double ah,
                                          double bx, double by, double bw, double bh) {
        return ax < bx + bw && ax + aw > bx && ay < by + bh && ay + ah > by;
    }
}