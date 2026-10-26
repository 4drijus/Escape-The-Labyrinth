package lt.escape.labyrinth.server.collision;

import java.awt.geom.Rectangle2D;

// Target interface used by PlayerPhysics
public interface CollisionObstacle {

    Rectangle2D getBounds();

    boolean blocksMovement();
}