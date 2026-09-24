package lt.escape.labyrinth.interactive;

public abstract class PressurePlate extends InteractiveElement {
    protected final Door door;
    protected PressurePlate(Door door) {
        this.door = java.util.Objects.requireNonNull(door);
    }
    public abstract void trigger();
    public abstract void release();
}
