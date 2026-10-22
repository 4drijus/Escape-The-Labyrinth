package lt.escape.labyrinth.server.interactive;

public final class TimedElementFactory implements InteractiveElementFactory {
    @Override public Door createDoor() { return new TimedDoor(3.0); }
    @Override public Button createButton(Mechanism target) { return new TimedButton(target); }
    @Override public PressurePlate createPressurePlate(Mechanism target) {
        return new TimedPressurePlate(target);
    }
}
