package lt.escape.labyrinth.server.interactive;

public abstract class PressurePlate extends Switch {
    protected PressurePlate(Mechanism target) {
        super(target);
    }
    public abstract void trigger();
    public abstract void release();
    // Bridge: standing on the plate triggers it, stepping off releases it.
    @Override public void onPlayerEnter() { trigger(); }
    @Override public void onPlayerLeave() { release(); }
    // Same as Button.copyFor(): the copy is linked to the copied mechanism.
    @Override public abstract PressurePlate copyFor(Mechanism target);
}
