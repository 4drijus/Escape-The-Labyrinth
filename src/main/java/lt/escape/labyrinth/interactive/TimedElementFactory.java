package lt.escape.labyrinth.interactive;

public final class TimedElementFactory implements InteractiveElementFactory {
    @Override public Door createDoor() { return new TimedDoor(3.0); }
    @Override public Button createButton(Door door) { return new TimedButton(door); }
    @Override public PressurePlate createPressurePlate(Door door) {
        return new TimedPressurePlate(door);
    }
}
