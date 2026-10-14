package lt.escape.labyrinth.server.world;

import lt.escape.labyrinth.server.interactive.Door;

/**
 * "Raktas – Naudojamas užrakintų durų atrakinimui" -- opens a Door,
 * the same way a Button, Lever, or PressurePlate can. No separate
 * "locked" state or LockedDoor subclass needed: NormalDoor/TimedDoor
 * already support open() from any trigger source.
 */
public class Key extends Inventory {
    public void use(Door door) {
        door.open();
    }
}