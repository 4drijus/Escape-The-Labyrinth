package lt.escape.labyrinth.server.interactive;

public final class NormalPressurePlate extends PressurePlate {
    public NormalPressurePlate(Mechanism target) { super(target); }
    @Override public NormalPressurePlate copyFor(Mechanism target) { return copyBoundsTo(new NormalPressurePlate(target)); }
    @Override public void trigger() { target.activate(); }
    @Override public void release() { target.deactivate(); }
}
