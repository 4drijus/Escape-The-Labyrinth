package lt.escape.labyrinth.server.enemy;

import lt.escape.labyrinth.shared.PlayerState;

import java.util.List;

/**
 * Strategy interface (the "Behavior" role in the class diagram).
 * A concrete Behavior decides how an Enemy moves on each game tick.
 * Enemy holds a reference to one of these and delegates movement to it,
 * so new behaviors can be added without touching the Enemy hierarchy,
 * and an enemy's behavior can be swapped at runtime.
 */
public interface Behavior {
    void move(Enemy enemy, List<PlayerState> players, double deltaTime);
}