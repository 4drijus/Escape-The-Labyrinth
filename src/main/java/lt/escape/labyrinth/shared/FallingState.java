package lt.escape.labyrinth.shared;

public final class FallingState implements MovementState {
    static final FallingState INSTANCE = new FallingState();

    private FallingState() {
    }

    @Override
    public void handleCommand(PlayerState player, PlayerCommand command) {
        MovementSupport.applyHorizontalCommand(player, command);
    }

    @Override
    public void afterPhysics(PlayerState player) {
        if (player.isOnGround()) {
            MovementSupport.enterGrounded(player);
        } else if (player.getVelocityY() < 0) {
            player.setMovementState(RisingState.INSTANCE);
        }
    }
}
