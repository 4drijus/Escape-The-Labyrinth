package lt.escape.labyrinth.server.world;

/**
 * Base class for anything placed in a level at a fixed position: tiles,
 * walls, movable objects. The original sketch only had positionX/positionY;
 * width/height are added here because collision (Wall blocking a Turret,
 * a puddle's trigger area) needs a bounding box, not just a point.
 */
public abstract class GameObject {
    protected double positionX;
    protected double positionY;
    protected double width;
    protected double height;

    protected GameObject(double positionX, double positionY, double width, double height) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.width = width;
        this.height = height;
    }

    public double getPositionX() {
        return positionX;
    }

    public double getPositionY() {
        return positionY;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    /** Axis-aligned bounding box overlap check, used for all collision in this package. */
    public boolean intersects(double x, double y, double w, double h) {
        return positionX < x + w
                && positionX + width > x
                && positionY < y + h
                && positionY + height > y;
    }
}