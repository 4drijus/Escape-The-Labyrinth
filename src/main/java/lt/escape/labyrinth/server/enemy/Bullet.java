package lt.escape.labyrinth.server.enemy;

import lt.escape.labyrinth.shared.BulletType;
import lt.escape.labyrinth.shared.PlayerState;

/**
 * A projectile fired by a Turret. Deliberately NOT an Enemy subclass —
 * it doesn't have a Behavior/movement strategy, it isn't drawn from the
 * Enemy hierarchy, and it has its own lifecycle (it dies on impact or
 * once it leaves the level).
 * Product in the Factory Method pattern.
 */
public abstract class Bullet {

    private double x;
    private double y;
    private final double velocityX;
    private final double velocityY;

    /** Simple circle-collision radius, in pixels. Tune to taste. */
    private static final double HIT_RADIUS = 12;

    private boolean active = true;

    protected Bullet(double x, double y, double directionX, double directionY, double speed) {
        this.x = x;
        this.y = y;
        this.velocityX = directionX * speed;
        this.velocityY = directionY * speed;
    }

    /** Called by the server when this bullet hits a player. */
    public abstract void onHit(PlayerState player);

    /** Tells the client which sprite to draw for this bullet. */
    public abstract BulletType getType();

    public void update(double deltaTime) {
        x += velocityX * deltaTime;
        y += velocityY * deltaTime;
    }

    public boolean isOutOfBounds(double levelWidth, double levelHeight) {
        return x < 0 || x > levelWidth || y < 0 || y > levelHeight;
    }

    /** Simple distance check against a player's position. */
    public boolean hits(PlayerState player) {
        double dx = player.getX() - x;
        double dy = player.getY() - y;
        return Math.sqrt(dx * dx + dy * dy) <= HIT_RADIUS;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        active = false;
    }
}
