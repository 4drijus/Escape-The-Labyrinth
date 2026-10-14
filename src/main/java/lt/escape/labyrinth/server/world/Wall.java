package lt.escape.labyrinth.server.world;

/**
 * Solid, impassable terrain. Enemy movement (PatrolBehavior, and any
 * future chase-style Behavior) must treat this as a hard boundary --
 * see Behavior.move()'s walls parameter.
 */
public class Wall extends Tile {
    public Wall(double positionX, double positionY, double width, double height) {
        super(positionX, positionY, width, height);
    }
}