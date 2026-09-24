package lt.escape.labyrinth.interactive;

public abstract class InteractiveElement {
    public void update(double deltaSeconds) {
        if (!Double.isFinite(deltaSeconds) || deltaSeconds < 0) {
            throw new IllegalArgumentException("deltaSeconds must be finite and nonnegative");
        }
    }
}
