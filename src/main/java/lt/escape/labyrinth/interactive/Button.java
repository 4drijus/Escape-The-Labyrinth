package lt.escape.labyrinth.interactive;

public abstract class Button extends InteractiveElement {
    protected final Door door;
    protected Button(Door door) {
        this.door = java.util.Objects.requireNonNull(door);
    }
    public abstract void press();
}
