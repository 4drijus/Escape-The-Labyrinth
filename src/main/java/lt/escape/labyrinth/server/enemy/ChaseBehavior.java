package lt.escape.labyrinth.server.enemy;

import lt.escape.labyrinth.server.world.Wall;
import lt.escape.labyrinth.shared.PlayerState;

import java.util.List;

/**
 * Strategy: follows the nearest player (the design doc's "Seka artimiausią
 * žaidėją") and hovers keepDistance pixels away from them instead of
 * landing on top of them. The only strategy that actually uses the
 * `players` argument of move().
 *
 * Each axis is moved separately, so a Wall blocks only the axis that hit it
 * and the enemy slides along the wall instead of freezing.
 */
public class ChaseBehavior implements Behavior {
    private final double speed;
    private final double keepDistance;

    public ChaseBehavior(double speed, double keepDistance) {
        this.speed = speed;
        this.keepDistance = keepDistance;
    }

    @Override
    public void move(Enemy enemy, List<PlayerState> players, List<Wall> walls, double deltaTime) {
        PlayerState target = nearest(enemy, players);
        if (target == null) {
            return;
        }

        double dx = target.getX() - enemy.getX();
        double dy = target.getY() - enemy.getY();
        double distance = Math.hypot(dx, dy);
        if (distance <= keepDistance) {
            return; // close enough, stay put and shoot
        }

        double step = Math.min(speed * deltaTime, distance - keepDistance);
        double newX = enemy.getX() + dx / distance * step;
        double newY = enemy.getY() + dy / distance * step;

        if (!enemy.wouldHitWall(newX, enemy.getY(), walls)) {
            enemy.setX(newX);
        }
        if (!enemy.wouldHitWall(enemy.getX(), newY, walls)) {
            enemy.setY(newY);
        }
    }

    private PlayerState nearest(Enemy enemy, List<PlayerState> players) {
        PlayerState best = null;
        double bestDistance = Double.MAX_VALUE;
        for (PlayerState player : players) {
            double distance = Math.hypot(player.getX() - enemy.getX(), player.getY() - enemy.getY());
            if (distance < bestDistance) {
                bestDistance = distance;
                best = player;
            }
        }
        return best;
    }
}