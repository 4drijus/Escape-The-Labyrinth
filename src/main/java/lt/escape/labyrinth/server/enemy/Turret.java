package lt.escape.labyrinth.server.enemy;

import lt.escape.labyrinth.server.world.Wall;
import lt.escape.labyrinth.shared.Player;

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

    private final int firingIntervalMs;
    private long msSinceLastShot = 0;

    protected Turret(double x, double y, int firingIntervalMs) {
        super(x, y, new StationaryBehavior());
        this.firingIntervalMs = firingIntervalMs;
    }

    protected Turret(Turret source) {
        super(source);
        this.firingIntervalMs = source.firingIntervalMs;
        this.msSinceLastShot = source.msSinceLastShot; // so we can have 0 at game restart
    }

    /** Factory method: each concrete turret decides which Bullet it fires. */
    protected abstract Bullet createBullet(double x, double y, double directionX, double directionY);

    @Override
    public List<Bullet> update(List<Player> players, List<Wall> walls, double deltaTime) {
        super.update(players, walls, deltaTime); // still lets the Behavior run

        msSinceLastShot += (long) (deltaTime * 1000);
        if (msSinceLastShot < firingIntervalMs || players.isEmpty()) {
            return Collections.emptyList();
        }

        msSinceLastShot = 0;
        Player target = nearestPlayer(players);
        return List.of(fire(target));
    }

    private Bullet fire(Player target) {
        double dx = target.getX() - positionX;
        double dy = target.getY() - positionY;
        double length = Math.sqrt(dx * dx + dy * dy);

        if (length == 0) {
            length = 1; // avoid divide-by-zero if the turret sits on the player
        }

        double directionX = dx / length;
        double directionY = dy / length;

        return createBullet(positionX, positionY, directionX, directionY);
    }

    private Player nearestPlayer(List<Player> players) {
        Player nearest = players.get(0);
        double bestDistSq = distanceSquared(nearest);

        for (Player player : players) {
            double distSq = distanceSquared(player);
            if (distSq < bestDistSq) {
                bestDistSq = distSq;
                nearest = player;
            }
        }
        return nearest;
    }

    private double distanceSquared(Player player) {
        double dx = player.getX() - positionX;
        double dy = player.getY() - positionY;
        return dx * dx + dy * dy;
    }
}