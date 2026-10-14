package lt.escape.labyrinth.server.world;

/**
 * "Dėžė – Galima stumdyti ar sugriauti, siekiant nueiti į nepasiekiamą
 * vietą." Pushable horizontally, or destroyed outright. Also usable to
 * press a PressurePlate without a player standing on it (per the doc),
 * which just means: once pushed onto a plate's area, call
 * PressurePlate.trigger() the same way a player would -- not wired up
 * here since it depends on your collision-detection loop in GameServer.
 *
 * moveUp()/moveDown() are no-ops: boxes only push left/right in this game.
 *
 * NOTE: nothing here detects a player pushing against the box and calls
 * moveLeft()/moveRight() automatically -- that trigger needs to live in
 * GameServer's per-tick player/box collision check, which doesn't exist
 * yet. This class is the piece that moves once told to.
 */
public class Box extends GameObject implements MoveableObj {
    private boolean movable = true;
    private boolean destroyed = false;
    private static final double PUSH_SPEED = 30; // pixels per push call; tune to taste

    public Box(double positionX, double positionY, double width, double height) {
        super(positionX, positionY, width, height);
    }

    public boolean isMovable() {
        return movable && !destroyed;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void destroy() {
        destroyed = true;
        movable = false;
    }

    @Override
    public void moveUp() {
        // Boxes push horizontally only; no-op.
    }

    @Override
    public void moveDown() {
        // Boxes push horizontally only; no-op.
    }

    @Override
    public void moveRight() {
        if (isMovable()) {
            positionX += PUSH_SPEED;
        }
    }

    @Override
    public void moveLeft() {
        if (isMovable()) {
            positionX -= PUSH_SPEED;
        }
    }
}