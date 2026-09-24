package lt.escape.labyrinth.interactive;

public abstract class Door extends InteractiveElement {
    private boolean open;
    public void open() { open = true; }
    public void close() { open = false; }
    public boolean isOpen() { return open; }
}
