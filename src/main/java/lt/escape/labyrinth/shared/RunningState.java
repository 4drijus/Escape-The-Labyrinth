package lt.escape.labyrinth.shared;

public final class RunningState implements MovementState {
    static final RunningState INSTANCE = new RunningState();

    private RunningState() {
    }

    @Override
    public boolean isAllowed(PlayerCommand command) {
        return command == PlayerCommand.LEFT
                || command == PlayerCommand.RIGHT
                || command == PlayerCommand.STOP
                || command == PlayerCommand.JUMP;
    }
}
