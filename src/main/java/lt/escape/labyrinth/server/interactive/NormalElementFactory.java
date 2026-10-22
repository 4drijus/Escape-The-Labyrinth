package lt.escape.labyrinth.server.interactive;

public final class NormalElementFactory implements InteractiveElementFactory {
    @Override public Door createDoor() { return new NormalDoor(); }
    @Override public Button createButton(Mechanism target) { return new NormalButton(target); }
    @Override public PressurePlate createPressurePlate(Mechanism target) {
        return new NormalPressurePlate(target);
    }
}
