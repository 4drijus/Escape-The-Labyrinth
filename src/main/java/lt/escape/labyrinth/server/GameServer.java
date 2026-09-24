package lt.escape.labyrinth.server;

import lt.escape.labyrinth.shared.GameState;
import lt.escape.labyrinth.shared.PlayerState;
import lt.escape.labyrinth.shared.EnemyState;
import lt.escape.labyrinth.shared.BulletState;
import lt.escape.labyrinth.shared.EnemyType;
import lt.escape.labyrinth.server.enemy.Enemy;
import lt.escape.labyrinth.server.enemy.SpikyBush;
import lt.escape.labyrinth.server.enemy.Turret;
import lt.escape.labyrinth.server.enemy.Bullet;

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

    // Matches the bounds already used in updatePlayer() below.
    private static final double LEVEL_WIDTH = 960;
    private static final double LEVEL_HEIGHT = 480;

    private static final double DELTA_TIME_SECONDS = 16.0 / 1000.0;

    // TODO: replace this hardcoded placement with real level data once level loading exists
    private static final List<Enemy> enemies = new ArrayList<>(List.of(
            new SpikyBush(300, 480, 1),
            new Turret(700, 200, 1, 2000)
    ));

    private static final List<Bullet> bullets = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("Starting the server");

        gameState = GameState.getInstance();
        System.out.println("Game state created");

        initEnemyStates();

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
        List<PlayerState> players = List.of(player1, player2);

        updatePlayer(player1);
        updatePlayer(player2);

        updateEnemies(players);
        updateBullets(players);
        checkContactDamage(players);

        broadcastGameState();
    }

    /** Mirrors the (fixed, hardcoded-for-now) enemies list into gameState's shared EnemyState snapshots, one time, at startup. */
    private static void initEnemyStates() {
        for (Enemy enemy : enemies) {
            EnemyType type = (enemy instanceof SpikyBush) ? EnemyType.SPIKY_BUSH : EnemyType.TURRET;
            gameState.getEnemies().add(new EnemyState(type, enemy.getX(), enemy.getY()));
        }
    }

    private static void updateEnemies(List<PlayerState> players) {
        List<EnemyState> enemyStates = gameState.getEnemies();

        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            bullets.addAll(enemy.update(players, DELTA_TIME_SECONDS));

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

            if (bullet.isOutOfBounds(LEVEL_WIDTH, LEVEL_HEIGHT)) {
                it.remove();
                continue;
            }

            for (PlayerState player : players) {
                if (bullet.hits(player)) {
                    player.takeDamage(bullet.getDamage());
                    it.remove();
                    break;
                }
            }
        }

        List<BulletState> bulletStates = new ArrayList<>();
        for (Bullet bullet : bullets) {
            bulletStates.add(new BulletState(bullet.getX(), bullet.getY()));
        }
        gameState.setBullets(bulletStates);
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