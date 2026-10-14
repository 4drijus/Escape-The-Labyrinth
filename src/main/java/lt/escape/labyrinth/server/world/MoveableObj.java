package lt.escape.labyrinth.server.world;

/**
 * Renamed from the diagram's "moveableObj" to follow Java class-naming
 * convention (types start with a capital letter) -- same contract.
 */
public interface MoveableObj {
    void moveUp();

    void moveDown();

    void moveRight();

    void moveLeft();
}