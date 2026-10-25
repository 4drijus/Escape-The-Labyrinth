package lt.escape.labyrinth.server.collision;

import java.awt.geom.Rectangle2D;
import java.util.Objects;

import lt.escape.labyrinth.server.interactive.Door;

/**
 * Adapts Door to the CollisionObstacle interface.
 * Reads the current door state whenever physics checks it.
 */
public final class DoorCollisionAdapter implements CollisionObstacle {

    private final Door door;

    public DoorCollisionAdapter(Door door) {
        this.door = Objects.requireNonNull(door);
    }

    @Override
    public Rectangle2D getBounds() {
        return new Rectangle2D.Double(
                door.getX(),
                door.getY(),
                door.getWidth(),
                door.getHeight()
        );
    }

    @Override
    public boolean blocksMovement() {
        return !door.isOpen();
    }
}