package lt.escape.labyrinth.server;

import lt.escape.labyrinth.server.enemy.Bullet;
import lt.escape.labyrinth.server.level.Level;
import lt.escape.labyrinth.shared.PlayerState;
import lt.escape.labyrinth.shared.Prototype;

import java.util.ArrayList;
import java.util.List;

/**
 * Prototype: a saved moment of the game -- the level as it is right now,
 * both players and the bullets in flight. GameWorld saves a copy() of
 * the live game and loads a copy() of the saved one, so the same
 * checkpoint can be loaded any number of times.
 *
 * levelNumber says which level of the sequence it was saved on, so loading
 * it from a later level also takes the game back to that level.
 */
public class Checkpoint implements Prototype<Checkpoint> {
    private final int levelNumber;
    private final Level level;
    private final PlayerState player1;
    private final PlayerState player2;
    private final List<Bullet> bullets;

    public Checkpoint(int levelNumber, Level level, PlayerState player1, PlayerState player2, List<Bullet> bullets) {
        this.levelNumber = levelNumber;
        this.level = level;
        this.player1 = player1;
        this.player2 = player2;
        this.bullets = bullets;
    }

    @Override
    public Checkpoint copy() {
        List<Bullet> bulletCopies = new ArrayList<>();
        for (Bullet bullet : bullets) {
            bulletCopies.add(bullet.copy());
        }
        return new Checkpoint(levelNumber, level.copy(), player1.copy(), player2.copy(), bulletCopies);
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public Level getLevel() {
        return level;
    }

    public PlayerState getPlayer1() {
        return player1;
    }

    public PlayerState getPlayer2() {
        return player2;
    }

    public List<Bullet> getBullets() {
        return bullets;
    }
}
