package lt.escape.labyrinth.server.interactive;

public final class TimedPressurePlate extends PressurePlate {
    public TimedPressurePlate(Mechanism target) { super(target); }
    @Override public TimedPressurePlate copyFor(Mechanism target) { return copyBoundsTo(new TimedPressurePlate(target)); }
    @Override public void trigger() { target.activate(); }
    @Override public void release() {
        // Leaving the plate does not cancel the door's countdown.
    }
}
