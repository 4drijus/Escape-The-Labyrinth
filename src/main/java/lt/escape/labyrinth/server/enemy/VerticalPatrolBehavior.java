package lt.escape.labyrinth.server.enemy;

import lt.escape.labyrinth.server.world.Wall;
import lt.escape.labyrinth.shared.PlayerState;

import java.util.List;

/**
 * Strategy: moves up and down around its starting y, up to range pixels
 * each way, turning around early if a Wall is in the way.
 */
public class VerticalPatrolBehavior implements Behavior {
    private final double range;
    private final double speed;

    private double startY;
    private boolean initialized = false;
    private boolean movingUp = true;

    public VerticalPatrolBehavior(double range, double speed) {
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
            startY = enemy.getY();
            initialized = true;
        }

        double step = speed * deltaTime * (movingUp ? -1 : 1);
        double newY = enemy.getY() + step;

        if (newY < startY - range) {
            newY = startY - range;
            movingUp = false;
        } else if (newY > startY + range) {
            newY = startY + range;
            movingUp = true;
        }

        if (enemy.wouldHitWall(enemy.getX(), newY, walls)) {
            movingUp = !movingUp;
            return;
        }

        enemy.setY(newY);
    }
}