package lt.escape.labyrinth.server.enemy;

import lt.escape.labyrinth.shared.PlayerState;

/**
 * A projectile fired by a Turret. Deliberately NOT an Enemy subclass —
 * it doesn't have a Behavior/movement strategy, it isn't drawn from the
 * Enemy hierarchy, and it has its own lifecycle (it dies on impact or
 * once it leaves the level).
 */
public class Bullet {

    private double x;
    private double y;
    private final double velocityX;
    private final double velocityY;
    private final int damage;

    /** Simple circle-collision radius, in pixels. Tune to taste. */
    private static final double HIT_RADIUS = 12;

    private boolean active = true;

    public Bullet(double x, double y, double velocityX, double velocityY, int damage) {
        this.x = x;
        this.y = y;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.damage = damage;
    }

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

    public int getDamage() {
        return damage;
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        active = false;
    }
}