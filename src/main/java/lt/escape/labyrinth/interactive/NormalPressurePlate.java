package lt.escape.labyrinth.interactive;

public final class NormalPressurePlate extends PressurePlate {
    public NormalPressurePlate(Door door) { super(door); }
    @Override public void trigger() { door.open(); }
    @Override public void release() { door.close(); }
}
