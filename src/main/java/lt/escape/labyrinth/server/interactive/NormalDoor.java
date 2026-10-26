package lt.escape.labyrinth.server.interactive;

public final class NormalDoor extends Door {
    private boolean unlocked;

    public NormalDoor() {
    }

    private NormalDoor(NormalDoor source) {
        super(source);
        unlocked = source.unlocked;
    }

    public void unlock() {
        unlocked = true;
        super.open();
    }

    @Override
    public void close() {
        if (!unlocked) {
            super.close();
        }
    }

    @Override
    public NormalDoor copy() {
        return new NormalDoor(this);
    }
}