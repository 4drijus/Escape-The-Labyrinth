package lt.escape.labyrinth.server.interactive;

public final class NormalPressurePlate extends PressurePlate {
    public NormalPressurePlate(Door door) { super(door); }
    @Override public NormalPressurePlate copyFor(Door door) { return copyBoundsTo(new NormalPressurePlate(door)); }
    @Override public void trigger() { door.open(); }
    @Override public void release() { door.close(); }
}
