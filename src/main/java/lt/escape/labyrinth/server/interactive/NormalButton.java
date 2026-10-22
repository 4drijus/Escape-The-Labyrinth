package lt.escape.labyrinth.server.interactive;

public final class NormalButton extends Button {
    public NormalButton(Mechanism target) { super(target); }
    @Override public NormalButton copyFor(Mechanism target) { return copyBoundsTo(new NormalButton(target)); }
    @Override public void press() {
        // A normal switch toggles the door.
        if (target.isActive()) target.deactivate();
        else target.activate();
    }
}
