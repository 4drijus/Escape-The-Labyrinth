package lt.escape.labyrinth.server.interactive;

public final class NormalButton extends Button {

    public NormalButton(Mechanism target) {
        super(target);
    }

    @Override
    public void press() {
        if (target instanceof NormalDoor normalDoor) {
            normalDoor.unlock();
        } else {
            target.activate();
        }
    }

    @Override
    public NormalButton copyFor(Mechanism target) {
        return copyBoundsTo(new NormalButton(target));
    }
}
