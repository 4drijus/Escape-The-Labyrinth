package lt.escape.labyrinth.shared;

public final class RunningState implements MovementState {
    static final RunningState INSTANCE = new RunningState();

    private RunningState() {
    }

    @Override
    public void handleCommand(PlayerState player, PlayerCommand command) {
        if (command == PlayerCommand.JUMP) {
            MovementSupport.jump(player);
        } else {
            MovementSupport.applyHorizontalCommand(player, command);
            if (player.getVelocityX() == 0) {
                player.setMovementState(IdleState.INSTANCE);
            }
        }
    }

    @Override
    public void afterPhysics(PlayerState player) {
        if (!player.isOnGround()) {
            MovementSupport.enterAirborne(player);
        } else if (player.getVelocityX() == 0) {
            player.setMovementState(IdleState.INSTANCE);
        }
    }
}
