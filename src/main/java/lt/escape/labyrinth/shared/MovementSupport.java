package lt.escape.labyrinth.shared;

final class MovementSupport {
    private static final double MOVE_SPEED = 4;
    private static final double JUMP_VELOCITY = -8;

    private MovementSupport() {
    }

    static void applyHorizontalCommand(PlayerState player, PlayerCommand command) {
        switch (command) {
            case LEFT -> player.setVelocityX(-MOVE_SPEED);
            case RIGHT -> player.setVelocityX(MOVE_SPEED);
            case STOP -> player.setVelocityX(0);
            default -> { }
        }
    }

    static void jump(PlayerState player) {
        player.setVelocityY(JUMP_VELOCITY);
        player.setOnGround(false);
        player.setMovementState(RisingState.INSTANCE);
    }

    static void enterGrounded(PlayerState player) {
        player.setMovementState(player.getVelocityX() == 0 ? IdleState.INSTANCE : RunningState.INSTANCE);
    }

    static void enterAirborne(PlayerState player) {
        player.setMovementState(player.getVelocityY() < 0 ? RisingState.INSTANCE : FallingState.INSTANCE);
    }
}
