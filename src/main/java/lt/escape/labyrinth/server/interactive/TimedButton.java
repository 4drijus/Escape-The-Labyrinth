package lt.escape.labyrinth.server.interactive;

public final class TimedButton extends Button {
    public TimedButton(Mechanism target) { super(target); }
    @Override public TimedButton copyFor(Mechanism target) { return copyBoundsTo(new TimedButton(target)); }
    @Override public void press() {
        // Starts or restarts the timed door's opening interval.
        target.activate();
    }
}
