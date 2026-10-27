package lt.escape.labyrinth.server.interactive;

import java.util.Objects;

/**
 * BRIDGE: Abstraction. A switch is something a player operates -- press a
 * Button, stand on a PressurePlate, pull a Lever. WHAT it operates is the
 * Mechanism it holds (a Door, a Lift, ...).
 *
 * The two hierarchies vary independently: without the bridge every pair
 * would need its own class (ButtonDoor, ButtonLift, LeverDoor, LeverLift ...).
 */
public abstract class Switch extends InteractiveElement {
    protected final Mechanism target;

    protected Switch(Mechanism target) {
        this.target = Objects.requireNonNull(target);
    }

    public Mechanism getTarget() {
        return target;
    }

    /** A player stepped onto / touched the switch. */
    public abstract void onPlayerEnter();

    /** The last player left the switch. Most switches don't care. */
    public void onPlayerLeave() {
    }

    /** Prototype: a copy of this switch that operates the given (copied) mechanism. */
    public abstract Switch copyFor(Mechanism target);
}
