package lt.escape.labyrinth.interactive;

public interface InteractiveElementFactory {
    Door createDoor();
    Button createButton(Door door);
    PressurePlate createPressurePlate(Door door);
}
