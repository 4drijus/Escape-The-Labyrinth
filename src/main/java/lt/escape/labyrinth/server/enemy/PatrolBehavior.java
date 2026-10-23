package lt.escape.labyrinth.server.enemy;

import lt.escape.labyrinth.server.world.Wall;
import lt.escape.labyrinth.shared.PlayerState;

import java.util.List;

/**
 * Strategy: walks left and right around its starting x, up to range
 * pixels each way, turning around early if a Wall is in the way.
 */
public class PatrolBehavior implements Behavior {
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
    public void reset() {
        initialized = false;
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

        if (enemy.wouldHitWall(newX, enemy.getY(), walls)) {
            movingRight = !movingRight; // don't enter the wall; go the other way next tick
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
}