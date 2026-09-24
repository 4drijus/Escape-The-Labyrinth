package lt.escape.labyrinth.server.enemy;

import lt.escape.labyrinth.shared.PlayerState;

/**
 * Concrete enemy. Always uses StationaryBehavior — it never needs a
 * different movement strategy, but could be given one later without
 * changing this class.
 */
public class SpikyBush extends Enemy {

    private final int damageAmount;

    /** Distance, in pixels, within which the bush counts as "touching" a player. Tune to your tile size. */
    private static final double TOUCH_RADIUS = 20;

    public SpikyBush(double x, double y, int damageAmount) {
        super(x, y, new StationaryBehavior());
        this.damageAmount = damageAmount;
    }

    public boolean isTouching(PlayerState player) {
        double dx = player.getX() - x;
        double dy = player.getY() - y;
        return Math.sqrt(dx * dx + dy * dy) <= TOUCH_RADIUS;
    }

    /** Called by the server when a player's hitbox overlaps this bush. */
    public void damage(PlayerState player) {
        player.takeDamage(damageAmount);
    }
}