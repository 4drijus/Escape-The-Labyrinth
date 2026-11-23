package lt.escape.labyrinth.server.enemy;

import lt.escape.labyrinth.server.world.Wall;
import lt.escape.labyrinth.shared.PlayerState;

import java.util.List;

/**
 * Strategy: sweeps left and right (rangeX) while bouncing up and down
 * (amplitudeY) at the same time, which traces a zig-zag. A Wall only
 * flips the axis that hit it, so the enemy bounces off instead of
 * stopping.
 */
public class ZigZagBehavior implements Behavior {
    private final double rangeX;
    private final double amplitudeY;
    private final double speed;

    private double startX;
    private double startY;
    private boolean initialized = false;
    private boolean movingRight = true;
    private boolean movingDown = true;

    public ZigZagBehavior(double rangeX, double amplitudeY, double speed) {
        this.rangeX = rangeX;
        this.amplitudeY = amplitudeY;
        this.speed = speed;
    }

    @Override
    public void reset() {
        initialized = false;
    }

    @Override
    public Behavior copy() {
        ZigZagBehavior copy = new ZigZagBehavior(rangeX, amplitudeY, speed);
        copy.startX = startX;
        copy.startY = startY;
        copy.initialized = initialized;
        copy.movingRight = movingRight;
        copy.movingDown = movingDown;
        return copy;
    }

    @Override
    public void move(Enemy enemy, List<PlayerState> players, List<Wall> walls, double deltaTime) {
        if (!initialized) {
            startX = enemy.getPositionX();
            startY = enemy.getPositionY();
            initialized = true;
        }

        double step = speed * deltaTime;
        double newX = enemy.getPositionX() + (movingRight ? step : -step);
        double newY = enemy.getPositionY() + (movingDown ? step : -step);

        if (newX > startX + rangeX) {
            newX = startX + rangeX;
            movingRight = false;
        } else if (newX < startX - rangeX) {
            newX = startX - rangeX;
            movingRight = true;
        }

        if (newY > startY + amplitudeY) {
            newY = startY + amplitudeY;
            movingDown = false;
        } else if (newY < startY - amplitudeY) {
            newY = startY - amplitudeY;
            movingDown = true;
        }

        // Resolve each axis separately so a wall only flips the axis that hit it.
        if (enemy.wouldHitWall(newX, enemy.getPositionY(), walls)) {
            movingRight = !movingRight;
            newX = enemy.getPositionX();
        }
        if (enemy.wouldHitWall(newX, newY, walls)) {
            movingDown = !movingDown;
            newY = enemy.getPositionY();
        }

        enemy.setX(newX);
        enemy.setY(newY);
    }
}