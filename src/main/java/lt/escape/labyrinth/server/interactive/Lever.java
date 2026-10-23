package lt.escape.labyrinth.server.interactive;

/**
 * "Svirtis – Nulenkiama, kad būtų aktyvuotas liftas/durys ar dar kažkoks
 * praėjimas (aktyvavimo laikas neribotas)." Unlike Button (timed), a
 * Lever's effect is permanent until pulled again.
 *
 * Targets a Door here, matching Button/PressurePlate's existing shape.
 * The doc also mentions levers operating lifts -- doing that would need
 * a shared interface Door and Lift both implement, which doesn't exist
 * yet, so it's not attempted here rather than guessed at.
 */
public class Lever extends InteractiveElement {
    private final Door door;
    private boolean state = false;

    public Lever(Door door) {
        this.door = door;
    }

    public boolean getState() {
        return state;
    }

    /** Prototype, like Button.copyFor(): same lever position, linked to the copied door. */
    public Lever copyFor(Door door) {
        Lever copy = copyBoundsTo(new Lever(door));
        copy.state = state;
        return copy;
    }

    public void pull() {
        state = !state;
        if (state) {
            door.open();
        } else {
            door.close();
        }
    }

    @Override
    public void update(double deltaSeconds) {
        super.update(deltaSeconds); // validates deltaSeconds, same as TimedDoor does
        // No time-based state of its own, unlike TimedButton/TimedDoor.
    }
}