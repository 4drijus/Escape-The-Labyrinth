package lt.escape.labyrinth.shared;

public class EnemyState {
    private final EnemyType type;
    private double x;
    private double y;

    public EnemyState(EnemyType type, double x, double y) {
        this.type = type;
        this.x = x;
        this.y = y;
    }

    public EnemyType getType() {
        return type;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
}