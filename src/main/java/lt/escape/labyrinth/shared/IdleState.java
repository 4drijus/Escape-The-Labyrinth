package lt.escape.labyrinth.shared;

public final class IdleState implements MovementState {
    static final IdleState INSTANCE = new IdleState();

    private IdleState() {
    }

    @Override
    public boolean isAllowed(PlayerCommand command) {
        return command == PlayerCommand.LEFT
                || command == PlayerCommand.RIGHT
                || command == PlayerCommand.STOP
                || command == PlayerCommand.JUMP;
    }
}
