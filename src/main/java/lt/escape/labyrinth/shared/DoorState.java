package lt.escape.labyrinth.shared;

/**
 * Render-only snapshot of a Door. Position is static (set once at level
 * load), but open/closed changes during play, so GameServer updates
 * setOpen() each tick the same way it updates EnemyState's x/y.
 */
public class DoorState {
    private final double x;
    private final double y;
    private final double width;
    private final double height;
    private boolean open;

    public DoorState(double x, double y, double width, double height, boolean open) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.open = open;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
}