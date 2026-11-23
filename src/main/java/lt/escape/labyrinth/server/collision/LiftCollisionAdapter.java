package lt.escape.labyrinth.server.collision;

import java.awt.geom.Rectangle2D;
import java.util.Objects;

import lt.escape.labyrinth.server.world.Lift;

/**
 * Adapts Lift to the CollisionObstacle interface.
 * Reads the lift's current position whenever physics checks it, so a player
 * standing on a moving lift rides it.
 */
public final class LiftCollisionAdapter implements CollisionObstacle {

    private final Lift lift;

    public LiftCollisionAdapter(Lift lift) {
        this.lift = Objects.requireNonNull(lift);
    }

    @Override
    public Rectangle2D getBounds() {
        return new Rectangle2D.Double(
                lift.getPositionX(),
                lift.getPositionY(),
                lift.getWidth(),
                lift.getHeight()
        );
    }

    @Override
    public boolean blocksMovement() {
        return true;
    }
}
