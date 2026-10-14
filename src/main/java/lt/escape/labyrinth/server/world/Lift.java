package lt.escape.labyrinth.server.world;

/**
 * "Liftas – Up up down down." Cycles vertically between its starting
 * position and movementRange pixels above it, reversing at each end.
 *
 * moveRight()/moveLeft() are no-ops: the interface asks for all four
 * directions but this object is vertical-only by design.
 *
 * NOTE: this only moves the lift's own position. Making a PlayerState
 * ride along with it (detect standing on top, carry them each tick)
 * isn't implemented here -- that's a separate player-physics feature.
 */
public class Lift extends GameObject implements MoveableObj {
    private final int movementRange;
    private final double startY;
    private boolean movingUp = true;
    private double speed = 40; // pixels per second; tune to taste

    public Lift(double positionX, double positionY, double width, double height, int movementRange) {
        super(positionX, positionY, width, height);
        this.movementRange = movementRange;
        this.startY = positionY;
    }

    public void update(double deltaTime) {
        double step = speed * deltaTime * (movingUp ? -1 : 1);
        positionY += step;

        if (positionY <= startY - movementRange) {
            positionY = startY - movementRange;
            movingUp = false;
        } else if (positionY >= startY) {
            positionY = startY;
            movingUp = true;
        }
    }

    @Override
    public void moveUp() {
        movingUp = true;
    }

    @Override
    public void moveDown() {
        movingUp = false;
    }

    @Override
    public void moveRight() {
        // Lift is vertical-only; no-op.
    }

    @Override
    public void moveLeft() {
        // Lift is vertical-only; no-op.
    }
}