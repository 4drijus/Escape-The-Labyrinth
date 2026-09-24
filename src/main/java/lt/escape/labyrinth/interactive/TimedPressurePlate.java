package lt.escape.labyrinth.interactive;

public final class TimedPressurePlate extends PressurePlate {
    public TimedPressurePlate(Door door) { super(door); }
    @Override public void trigger() { door.open(); }
    @Override public void release() {
        // Leaving the plate does not cancel the door's countdown.
    }
}
