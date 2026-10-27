package lt.escape.labyrinth.server.interactive;

public abstract class Button extends Switch {
    protected Button(Mechanism target) {
        super(target);
    }
    public abstract void press();
    // Bridge: touching a button presses it once.
    @Override public void onPlayerEnter() { press(); }
    // Prototype for linked elements: the copy must operate the copied
    // mechanism, not the original one, so the caller passes it in.
    @Override public abstract Button copyFor(Mechanism target);
}
