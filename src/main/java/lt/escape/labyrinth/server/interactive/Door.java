package lt.escape.labyrinth.server.interactive;

import lt.escape.labyrinth.shared.Prototype;

public abstract class Door extends InteractiveElement implements Prototype<Door> {
    private boolean open;
    protected Door() { }
    // Copy constructor for the subclasses' copy(): keeps open/closed and the position.
    protected Door(Door source) { open = source.open; source.copyBoundsTo(this); }
    public void open() { open = true; }
    public void close() { open = false; }
    public boolean isOpen() { return open; }
}
