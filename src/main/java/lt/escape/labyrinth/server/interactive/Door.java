package lt.escape.labyrinth.server.interactive;

import lt.escape.labyrinth.shared.Prototype;

public abstract class Door extends InteractiveElement implements Prototype<Door> {
    private boolean open;
    protected Door() { }
    // Copy constructor for the subclasses' copy(): keeps open/closed.
    protected Door(Door source) { open = source.open; }
    public void open() { open = true; }
    public void close() { open = false; }
    public boolean isOpen() { return open; }
}
