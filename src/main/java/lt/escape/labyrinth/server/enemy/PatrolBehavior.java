package lt.escape.labyrinth.server.enemy;

import lt.escape.labyrinth.server.world.Wall;
import lt.escape.labyrinth.shared.PlayerState;

import java.util.List;

/**
 * Concrete strategy: walks back and forth around its starting position,
 * up to range pixels in either direction -- and now also turns around
 * early if the next step would intersect a Wall.
 */
public class PatrolBehavior implements Behavior {
    // Matches the 30x30 size GamePanel already draws enemies at. Enemy
    // itself has no width/height field, so this is the best collision
    // box available without adding one to Enemy.
    private static final double ENEMY_SIZE = 30;

    private final double range;
    private final double speed;

    private double startX;
    private boolean initialized = false;
    private boolean movingRight = true;

    public PatrolBehavior(double range, double speed) {
        this.range = range;
        this.speed = speed;
    }

    @Override
    public void move(Enemy enemy, List<PlayerState> players, List<Wall> walls, double deltaTime) {
        if (!initialized) {
            startX = enemy.getX();
            initialized = true;
        }

        double step = speed * deltaTime * (movingRight ? 1 : -1);
        double newX = enemy.getX() + step;

        if (newX > startX + range) {
            newX = startX + range;
            movingRight = false;
        } else if (newX < startX - range) {
            newX = startX - range;
            movingRight = true;
        }

        if (hitsWall(newX, enemy.getY(), walls)) {
            // Don't move into the wall this tick; next tick starts moving the other way.
            movingRight = !movingRight;
            return;
        }

        enemy.setX(newX);
    }

    @Override
    public Behavior copy() {
        PatrolBehavior copy = new PatrolBehavior(range, speed);
        copy.startX = startX;
        copy.initialized = initialized;
        copy.movingRight = movingRight;
        return copy;
    }

    private boolean hitsWall(double x, double y, List<Wall> walls) {
        if (walls == null) {
            return false;
        }
        for (Wall wall : walls) {
            if (wall.intersects(x, y, ENEMY_SIZE, ENEMY_SIZE)) {
                return true;
            }
        }
        return false;
    }
}