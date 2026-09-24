package lt.escape.labyrinth.shared;

/** A render-only snapshot of a bullet's position, shared between server and client. */
public class BulletState {
    private final double x;
    private final double y;

    public BulletState(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}