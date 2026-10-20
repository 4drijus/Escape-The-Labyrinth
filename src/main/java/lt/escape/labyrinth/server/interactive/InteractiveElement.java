package lt.escape.labyrinth.server.interactive;

public abstract class InteractiveElement {
    private double x;
    private double y;
    private double width;
    private double height;

    public void setBounds(double x, double y, double width, double height) {
        this.x = x; this.y = y; this.width = width; this.height = height;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }

    public void update(double deltaSeconds) {
        if (!Double.isFinite(deltaSeconds) || deltaSeconds < 0) {
            throw new IllegalArgumentException("deltaSeconds must be finite and nonnegative");
        }
    }
}
