package lt.escape.labyrinth.server.enemy;

import lt.escape.labyrinth.shared.PlayerState;

import java.util.List;

/**
 * Concrete strategy: the enemy never moves.
 * Used by enemies like SpikyBush that just sit in place and damage
 * whatever touches them.
 */
public class StationaryBehavior implements Behavior {
    @Override
    public void move(Enemy enemy, List<PlayerState> players, double deltaTime) {
        // Intentionally does nothing.
    }
}