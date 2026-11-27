package lt.escape.labyrinth.server.enemy;

import lt.escape.labyrinth.server.world.Wall;
import lt.escape.labyrinth.shared.Player;

import java.util.List;

/** Concrete strategy: the enemy never moves, so walls are irrelevant here. */
public class StationaryBehavior implements Behavior {
    @Override
    public void move(Enemy enemy, List<Player> players, List<Wall> walls, double deltaTime) {
        // Intentionally does nothing.
    }

    @Override
    public Behavior copy() {
        return this;
    }
}