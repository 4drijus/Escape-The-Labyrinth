package lt.escape.labyrinth.shared;

public final class StateManager {
    static final StateManager INSTANCE = new StateManager();

    private StateManager() {
    }

    public MovementState nextState(Player player) {
        if (player.isOnGround()) {
            return player.getVelocityX() == 0
                    ? IdleState.INSTANCE
                    : RunningState.INSTANCE;
        }
        return player.getVelocityY() < 0
                ? RisingState.INSTANCE
                : FallingState.INSTANCE;
    }
}
