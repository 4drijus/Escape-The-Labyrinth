package lt.escape.labyrinth.server.interactive;

import lt.escape.labyrinth.server.world.GameObject;

/**
 * A GameObject the players interact with. The factories create these
 * without a position, so the level builder places them with setBounds().
 */
public abstract class InteractiveElement extends GameObject {

    public void setBounds(double x, double y, double width, double height) {
        this.positionX = x; this.positionY = y; this.width = width; this.height = height;
    }

    // For copy()/copyFor(): gives the copy this element's position and size.
    protected <T extends InteractiveElement> T copyBoundsTo(T copy) {
        copy.setBounds(positionX, positionY, width, height);
        return copy;
    }

    public void update(double deltaSeconds) {
        if (!Double.isFinite(deltaSeconds) || deltaSeconds < 0) {
            throw new IllegalArgumentException("deltaSeconds must be finite and nonnegative");
        }
    }
}
