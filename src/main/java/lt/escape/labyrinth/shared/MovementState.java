package lt.escape.labyrinth.shared;

public interface MovementState {
    boolean isAllowed(PlayerCommand command);
}
