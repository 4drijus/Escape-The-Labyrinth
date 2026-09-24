package lt.escape.labyrinth.shared;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private static final GameState instance = new GameState();

    private final PlayerState player1;
    private final PlayerState player2;

    private final List<EnemyState> enemies = new ArrayList<>();

    private List<BulletState> bullets = new ArrayList<>();

    private GameState() {
        player1 = new PlayerState(1, 500, 480);
        player2 = new PlayerState(2, 100, 480);
    }

    public static GameState getInstance() { return instance; }

    public PlayerState getPlayer1() {
        return player1;
    }

    public PlayerState getPlayer2() {
        return player2;
    }

    public PlayerState getPlayer(int playerId) {
        if (playerId == 1) {
            return player1;
        }

        if (playerId == 2) {
            return player2;
        }

        return null;
    }

    public List<EnemyState> getEnemies() {
        return enemies;
    }

    public List<BulletState> getBullets() {
        return bullets;
    }

    public void setBullets(List<BulletState> bullets) {
        this.bullets = bullets;
    }
}