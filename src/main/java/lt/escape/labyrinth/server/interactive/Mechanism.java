package lt.escape.labyrinth.server.interactive;

/**
 * BRIDGE: Implementor. Anything a Switch can operate -- a Door opens, a
 * Lift starts moving. Switches only know this interface, so any switch
 * works with any mechanism, and a new mechanism needs no new switch classes.
 */
public interface Mechanism {
    void activate();

    void deactivate();

    boolean isActive();
}
