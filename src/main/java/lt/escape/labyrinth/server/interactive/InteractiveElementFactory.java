package lt.escape.labyrinth.server.interactive;

public interface InteractiveElementFactory {
    Door createDoor();
    // Bridge: a switch takes any Mechanism, so the factory configures the bridge
    // (usually with the door it just made, but a Lift works too).
    Button createButton(Mechanism target);
    PressurePlate createPressurePlate(Mechanism target);
}
