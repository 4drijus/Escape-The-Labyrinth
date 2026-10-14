package lt.escape.labyrinth.server.enemy;

import lt.escape.labyrinth.server.world.Wall;
import lt.escape.labyrinth.shared.PlayerState;

import java.util.List;

/**
 * Strategy interface. A concrete Behavior decides how an Enemy moves on
 * each game tick.
 *
 * walls was added so movement strategies can respect level geometry --
 * PatrolBehavior now turns around at a Wall instead of only at its
 * fixed range. StationaryBehavior ignores it (it never moves anyway).
 * A future chase-style Behavior would use it the same way.
 */
public interface Behavior {
    void move(Enemy enemy, List<PlayerState> players, List<Wall> walls, double deltaTime);
}