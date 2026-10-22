package lt.escape.labyrinth.shared;

/**
 * Render-only snapshot of a Lever. Position is static (set once at level
 * load); whether it's pulled changes during play, like DoorState's open flag.
 */
public class LeverState {
    private final double x;
    private final double y;
    private final double width;
    private final double height;
    private boolean on;

    public LeverState(double x, double y, double width, double height, boolean on) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.on = on;
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

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }
}
