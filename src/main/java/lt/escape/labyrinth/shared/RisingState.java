package lt.escape.labyrinth.shared;

public final class RisingState implements MovementState {
    static final RisingState INSTANCE = new RisingState();

    private RisingState() {
    }

    @Override
    public void handleCommand(PlayerState player, PlayerCommand command) {
        MovementSupport.applyHorizontalCommand(player, command);
    }

    @Override
    public void afterPhysics(PlayerState player) {
        if (player.isOnGround()) {
            MovementSupport.enterGrounded(player);
        } else if (player.getVelocityY() >= 0) {
            player.setMovementState(FallingState.INSTANCE);
        }
    }
}
