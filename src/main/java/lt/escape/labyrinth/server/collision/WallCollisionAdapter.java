package lt.escape.labyrinth.server.collision;

import java.awt.geom.Rectangle2D;
import java.util.Objects;

import lt.escape.labyrinth.server.world.Wall;

 //Adapts Wall to the CollisionObstacle interface.
public final class WallCollisionAdapter implements CollisionObstacle {

    private final Wall wall;

    public WallCollisionAdapter(Wall wall) {
        this.wall = Objects.requireNonNull(wall);
    }

    @Override
    public Rectangle2D getBounds() {
        return new Rectangle2D.Double(
                wall.getPositionX(),
                wall.getPositionY(),
                wall.getWidth(),
                wall.getHeight()
        );
    }

    @Override
    public boolean blocksMovement() {
        return true;
    }
}