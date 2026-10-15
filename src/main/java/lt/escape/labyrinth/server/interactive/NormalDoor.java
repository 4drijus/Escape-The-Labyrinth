package lt.escape.labyrinth.server.interactive;

public final class NormalDoor extends Door {
    // Stays open until close() is called.
    public NormalDoor() { }
    private NormalDoor(NormalDoor source) { super(source); }
    @Override public NormalDoor copy() { return new NormalDoor(this); }
}
