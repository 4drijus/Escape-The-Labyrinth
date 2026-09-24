package lt.escape.labyrinth.interactive;

public final class AbstractFactoryDemo {
    public static void main(String[] args) {
        demonstrate(new NormalElementFactory());
        demonstrate(new TimedElementFactory());
    }

    public static void demonstrate(InteractiveElementFactory factory) {
        Door door = factory.createDoor();
        Button button = factory.createButton(door);
        PressurePlate plate = factory.createPressurePlate(door);

        System.out.println("\n" + factory.getClass().getSimpleName());
        System.out.println("Products: " + door.getClass().getSimpleName()
                + ", " + button.getClass().getSimpleName()
                + ", " + plate.getClass().getSimpleName());

        button.press();
        show("After button press", door);
        door.update(2.0);
        show("After 2 seconds", door);
        door.update(1.0);
        show("After 3 seconds total", door);

        door.close();
        plate.trigger();
        show("After stepping onto plate", door);
        plate.release();
        show("After leaving plate", door);
        door.update(3.0);
        show("After another 3 seconds", door);
    }

    private static void show(String action, Door door) {
        System.out.println(action + ": " + (door.isOpen() ? "OPEN" : "CLOSED"));
    }
}
