package lt.escape.labyrinth.shared;

public final class RisingState implements MovementState {
    static final RisingState INSTANCE = new RisingState();

    private RisingState() {
    }

    @Override
    public boolean isAllowed(PlayerCommand command) {
        return command == PlayerCommand.LEFT
                || command == PlayerCommand.RIGHT
                || command == PlayerCommand.STOP;
    }
}
