package lt.escape.labyrinth.shared;

/** Render-only snapshot of a Tile (puddle or bush). Static -- sent once, not every tick. */
public class TileState {
    private final TileType type;
    private final double x;
    private final double y;
    private final double width;
    private final double height;

    public TileState(TileType type, double x, double y, double width, double height) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public TileType getType() {
        return type;
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
}