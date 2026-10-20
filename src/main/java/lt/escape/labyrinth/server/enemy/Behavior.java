package lt.escape.labyrinth.server.enemy;

import lt.escape.labyrinth.server.world.Wall;
import lt.escape.labyrinth.shared.PlayerState;

import java.util.List;

/**
 * Strategy interface. A concrete Behavior decides how an Enemy moves on
 * each game tick. Enemy holds one and can swap it at runtime.
 */
public interface Behavior {
    void move(Enemy enemy, List<PlayerState> players, List<Wall> walls, double deltaTime);

    /**
     * Called by Enemy.setBehavior() whenever this strategy becomes the
     * active one. Strategies that remember a starting point (patrols,
     * zig-zag) forget it here, so they re-anchor at wherever the enemy
     * is NOW instead of snapping back to an old position.
     */
    default void reset() {
    }
}