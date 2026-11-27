package lt.escape.labyrinth.shared;

public final class FallingState implements MovementState {
    static final FallingState INSTANCE = new FallingState();

    private FallingState() {
    }

    @Override
    public boolean isAllowed(PlayerCommand command) {
        return command == PlayerCommand.LEFT
                || command == PlayerCommand.RIGHT
                || command == PlayerCommand.STOP;
    }
}
