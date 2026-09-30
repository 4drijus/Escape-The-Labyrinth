package lt.escape.labyrinth.shared;

/** A render-only snapshot of a bullet's position, shared between server and client. */
public class BulletState {
    private final BulletType type;
    private final double x;
    private final double y;

    public BulletState(BulletType type, double x, double y) {
        this.type = type;
        this.x = x;
        this.y = y;
    }

    public BulletType getType() {
        return type;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
