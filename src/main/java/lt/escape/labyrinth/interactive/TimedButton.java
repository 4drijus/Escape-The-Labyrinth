package lt.escape.labyrinth.interactive;

public final class TimedButton extends Button {
    public TimedButton(Door door) { super(door); }
    @Override public void press() {
        // Starts or restarts the timed door's opening interval.
        door.open();
    }
}
