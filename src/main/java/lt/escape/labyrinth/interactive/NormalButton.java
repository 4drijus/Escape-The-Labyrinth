package lt.escape.labyrinth.interactive;

public final class NormalButton extends Button {
    public NormalButton(Door door) { super(door); }
    @Override public void press() {
        // A normal switch toggles the door.
        if (door.isOpen()) door.close();
        else door.open();
    }
}
