package lt.escape.labyrinth.server.world;

import lt.escape.labyrinth.shared.Prototype;

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
public class Lift extends GameObject implements MoveableObj, Prototype<Lift> {
    private final int movementRange;
    private final double startY;
    private boolean movingUp = true;
    private double speed = 40; // pixels per second; tune to taste

    public Lift(double positionX, double positionY, double width, double height, int movementRange) {
        super(positionX, positionY, width, height);
        this.movementRange = movementRange;
        this.startY = positionY;
    }

    /**
     * Copy constructor rather than calling the public one: that would set
     * startY to wherever the lift is right now, shifting its whole range.
     */
    private Lift(Lift source) {
        super(source.positionX, source.positionY, source.width, source.height);
        this.movementRange = source.movementRange;
        this.startY = source.startY;
        this.movingUp = source.movingUp;
        this.speed = source.speed;
    }

    @Override
    public Lift copy() {
        return new Lift(this);
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