package lt.escape.labyrinth.server.enemy;

import lt.escape.labyrinth.shared.PlayerState;

import java.util.List;

/**
 * Concrete strategy: the enemy walks back and forth around its starting
 * position, up to {@code range} pixels in either direction.
 * A second, different algorithm to show the strategy is pluggable —
 * swap it in with enemy.setBehavior(new PatrolBehavior(...)).
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
    public void move(Enemy enemy, List<PlayerState> players, double deltaTime) {
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

        enemy.setX(newX);
    }
}