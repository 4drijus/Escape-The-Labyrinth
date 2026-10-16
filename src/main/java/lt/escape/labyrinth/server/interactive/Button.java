package lt.escape.labyrinth.server.interactive;

public abstract class Button extends InteractiveElement {
    protected final Door door;
    protected Button(Door door) {
        this.door = java.util.Objects.requireNonNull(door);
    }
    public abstract void press();
    // Prototype for door-linked elements: the copy must open the copied
    // door, not the original one, so the caller passes that door in.
    public abstract Button copyFor(Door door);
}
