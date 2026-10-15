package lt.escape.labyrinth.shared;

/**
 * Prototype pattern: an object that can make an independent copy of
 * itself, without the caller knowing its concrete class -- Level.copy()
 * calls enemy.copy() on a List<Enemy> and gets back a SpikyBush,
 * NormalTurret or InstaDeathTurret as appropriate.
 *
 * Implemented with copy() methods / copy constructors rather than
 * Cloneable/Object.clone(): several classes here have final fields
 * (e.g. Button's door, Lift's startY) that clone() could not reassign.
 */
public interface Prototype<T> {
    T copy();
}
