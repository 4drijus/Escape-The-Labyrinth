package lt.escape.labyrinth.interactive;

public final class NormalElementFactory implements InteractiveElementFactory {
    @Override public Door createDoor() { return new NormalDoor(); }
    @Override public Button createButton(Door door) { return new NormalButton(door); }
    @Override public PressurePlate createPressurePlate(Door door) {
        return new NormalPressurePlate(door);
    }
}
