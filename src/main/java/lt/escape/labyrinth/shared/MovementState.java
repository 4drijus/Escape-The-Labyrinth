package lt.escape.labyrinth.shared;

/**
 * A player's movement mode handles commands and reacts to the results of physics.
 * Implementations are stateless, so players and checkpoints can share them.
 */
public interface MovementState {
    void handleCommand(PlayerState player, PlayerCommand command);

    void afterPhysics(PlayerState player);
}
