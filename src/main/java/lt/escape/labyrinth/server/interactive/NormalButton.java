package lt.escape.labyrinth.server.interactive;

public final class NormalButton extends Button {

    public NormalButton(Door door) {
        super(door);
    }

    @Override
    public void press() {
        if (door instanceof NormalDoor normalDoor) {
            normalDoor.unlock();
        } else {
            door.open();
        }
    }

    @Override
    public NormalButton copyFor(Door door) {
        return copyBoundsTo(new NormalButton(door));
    }
}