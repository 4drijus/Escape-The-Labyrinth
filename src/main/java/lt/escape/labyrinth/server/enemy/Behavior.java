package lt.escape.labyrinth.server.enemy;

import lt.escape.labyrinth.server.world.Wall;
import lt.escape.labyrinth.shared.PlayerState;
import lt.escape.labyrinth.shared.Prototype;

import java.util.List;

/**
 * Strategy interface. A concrete Behavior decides how an Enemy moves on
 * each game tick.
 *
 * walls was added so movement strategies can respect level geometry --
 * PatrolBehavior now turns around at a Wall instead of only at its
 * fixed range. StationaryBehavior ignores it (it never moves anyway).
 * A future chase-style Behavior would use it the same way.
 *
 * Also a Prototype: Enemy.copy() copies its Behavior so a copied enemy
 * never shares movement state with the original.
 */
public interface Behavior extends Prototype<Behavior> {
    void move(Enemy enemy, List<PlayerState> players, List<Wall> walls, double deltaTime);
}