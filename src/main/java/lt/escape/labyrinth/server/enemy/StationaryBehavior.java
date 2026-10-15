package lt.escape.labyrinth.server.enemy;

import lt.escape.labyrinth.server.world.Wall;
import lt.escape.labyrinth.shared.PlayerState;

import java.util.List;

/** Concrete strategy: the enemy never moves, so walls are irrelevant here. */
public class StationaryBehavior implements Behavior {
    @Override
    public void move(Enemy enemy, List<PlayerState> players, List<Wall> walls, double deltaTime) {
        // Intentionally does nothing.
    }

    /**
     * Shallow on purpose: this class has no fields, so there is nothing a
     * copy could get out of sync with -- every enemy can share one instance.
     */
    @Override
    public Behavior copy() {
        return this;
    }
}