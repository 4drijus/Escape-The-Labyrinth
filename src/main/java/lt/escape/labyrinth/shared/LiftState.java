package lt.escape.labyrinth.shared;

/**
 * Render-only snapshot of a Lift. Size is fixed, but the lift moves
 * vertically, so GameState's copy is updated every tick like EnemyState's.
 */
public class LiftState {
    private final double x;
    private double y;
    private final double width;
    private final double height;

    public LiftState(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
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

    public void setY(double y) {
        this.y = y;
    }
}
