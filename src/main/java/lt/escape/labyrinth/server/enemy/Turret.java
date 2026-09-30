package lt.escape.labyrinth.server.enemy;

import lt.escape.labyrinth.shared.PlayerState;

import java.util.Collections;
import java.util.List;

/**
 * Abstract enemy. Also stationary by default, but firing logic is
 * independent of the Behavior strategy — the strategy only governs
 * movement, so Turret can swap in PatrolBehavior later (e.g. a turret
 * on a rail) without touching fire().
 * Creator in the Factory Method pattern.
 */
public abstract class Turret extends Enemy {

    protected static final double DEFAULT_BULLET_SPEED = 300.0;

    private final int firingIntervalMs;
    private final double bulletSpeed;
    private long msSinceLastShot = 0;

    protected Turret(double x, double y, int firingIntervalMs, double bulletSpeed) {
        super(x, y, new StationaryBehavior());
        this.firingIntervalMs = firingIntervalMs;
        this.bulletSpeed = bulletSpeed;
    }

    /** Factory method: each concrete turret decides which Bullet it fires. */
    protected abstract Bullet createBullet(double x, double y, double velocityX, double velocityY);

    @Override
    public List<Bullet> update(List<PlayerState> players, double deltaTime) {
        super.update(players, deltaTime); // still lets the Behavior run

        msSinceLastShot += (long) (deltaTime * 1000);
        if (msSinceLastShot < firingIntervalMs || players.isEmpty()) {
            return Collections.emptyList();
        }

        msSinceLastShot = 0;
        PlayerState target = nearestPlayer(players);
        return List.of(fire(target));
    }

    private Bullet fire(PlayerState target) {
        double dx = target.getX() - x;
        double dy = target.getY() - y;
        double length = Math.sqrt(dx * dx + dy * dy);

        if (length == 0) {
            length = 1; // avoid divide-by-zero if the turret sits on the player
        }

        double velocityX = (dx / length) * bulletSpeed;
        double velocityY = (dy / length) * bulletSpeed;

        return createBullet(x, y, velocityX, velocityY);
    }

    private PlayerState nearestPlayer(List<PlayerState> players) {
        PlayerState nearest = players.get(0);
        double bestDistSq = distanceSquared(nearest);

        for (PlayerState player : players) {
            double distSq = distanceSquared(player);
            if (distSq < bestDistSq) {
                bestDistSq = distSq;
                nearest = player;
            }
        }
        return nearest;
    }

    private double distanceSquared(PlayerState player) {
        double dx = player.getX() - x;
        double dy = player.getY() - y;
        return dx * dx + dy * dy;
    }
}
