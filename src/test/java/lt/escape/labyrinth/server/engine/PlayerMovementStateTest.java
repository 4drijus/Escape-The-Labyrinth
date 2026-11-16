package lt.escape.labyrinth.server.engine;

import lt.escape.labyrinth.server.interactive.NormalElementFactory;
import lt.escape.labyrinth.server.level.EasyLevelBuilder;
import lt.escape.labyrinth.server.level.Level;
import lt.escape.labyrinth.server.level.LevelLayout;
import lt.escape.labyrinth.shared.FallingState;
import lt.escape.labyrinth.shared.IdleState;
import lt.escape.labyrinth.shared.PlayerCommand;
import lt.escape.labyrinth.shared.PlayerState;
import lt.escape.labyrinth.shared.RisingState;
import lt.escape.labyrinth.shared.RunningState;

public class PlayerMovementStateTest {
    public static void main(String[] args) {
        testJumpAndLanding();
        testWalkingOffEdge();
        testCeilingCollision();
        testCheckpointAndPlacement();
        System.out.println("All movement state tests passed.");
    }

    private static Level level(LevelLayout layout) {
        return new EasyLevelBuilder(new NormalElementFactory(), layout).buildLevel();
    }

    private static void testJumpAndLanding() {
        PlayerPhysics physics = new PlayerPhysics();
        physics.load(level(LevelLayout.plains()));
        PlayerState player = new PlayerState(1, 500, 480);
        PlayerState other = new PlayerState(2, 100, 480);
        checkState(player, IdleState.class);
        physics.applyCommand(player, PlayerCommand.RIGHT);
        checkState(player, RunningState.class);
        checkState(other, IdleState.class);
        physics.applyCommand(player, PlayerCommand.JUMP);
        checkState(player, RisingState.class);
        check(player.getVelocityX() == 4 && player.getVelocityY() == -8, "running jump velocity");
        physics.update(player);
        double risingVelocity = player.getVelocityY();
        physics.applyCommand(player, PlayerCommand.JUMP);
        check(player.getVelocityY() == risingVelocity, "double jump while rising");
        for (int i = 0; i < 100 && player.getMovementState() instanceof RisingState; i++) {
            physics.update(player);
        }
        checkState(player, FallingState.class);
        check(player.getVelocityX() == 4, "horizontal movement lost at peak");
        double fallingVelocity = player.getVelocityY();
        physics.applyCommand(player, PlayerCommand.JUMP);
        check(player.getVelocityY() == fallingVelocity, "double jump while falling");
        land(physics, player);
        checkState(player, RunningState.class);
        physics.applyCommand(player, PlayerCommand.STOP);
        checkState(player, IdleState.class);

        physics.applyCommand(player, PlayerCommand.JUMP);
        physics.applyCommand(player, PlayerCommand.LEFT);
        checkState(player, RisingState.class);
        check(player.getVelocityX() == -4, "airborne direction change");
        physics.applyCommand(player, PlayerCommand.STOP);
        checkState(player, RisingState.class);
        for (int i = 0; i < 100 && player.getMovementState() instanceof RisingState; i++) {
            physics.update(player);
        }
        checkState(player, FallingState.class);
        physics.applyCommand(player, PlayerCommand.RIGHT);
        checkState(player, FallingState.class);
        physics.applyCommand(player, PlayerCommand.STOP);
        checkState(player, FallingState.class);
        land(physics, player);
        checkState(player, IdleState.class);
    }

    private static void testWalkingOffEdge() {
        PlayerPhysics physics = new PlayerPhysics();
        physics.load(level(LevelLayout.plains()));
        PlayerState player = new PlayerState(1, 358, 430);
        physics.applyCommand(player, PlayerCommand.RIGHT);
        physics.update(player);
        check(!player.isOnGround(), "player should have walked off the step");
        checkState(player, FallingState.class);
        land(physics, player);
        checkState(player, RunningState.class);
    }

    private static void testCeilingCollision() {
        PlayerPhysics physics = new PlayerPhysics();
        physics.load(level(LevelLayout.cave()));
        PlayerState player = new PlayerState(1, 200, 480);
        physics.applyCommand(player, PlayerCommand.JUMP);
        physics.update(player);
        check(player.getY() == 475 && player.getVelocityY() == 0, "ceiling collision");
        checkState(player, FallingState.class);
        land(physics, player);
        checkState(player, IdleState.class);
    }

    private static void testCheckpointAndPlacement() {
        PlayerPhysics physics = new PlayerPhysics();
        Level plains = level(LevelLayout.plains());
        physics.load(plains);
        PlayerState player = new PlayerState(1, 500, 480);
        PlayerState other = new PlayerState(2, 100, 480);
        physics.applyCommand(player, PlayerCommand.RIGHT);
        physics.applyCommand(player, PlayerCommand.JUMP);
        physics.update(player);
        PlayerState saved = player.copy();
        checkState(saved, RisingState.class);
        physics.applyCommand(player, PlayerCommand.STOP);
        player.restoreFrom(saved);
        checkState(player, RisingState.class);
        check(player.getVelocityX() == 0 && player.getVelocityY() == saved.getVelocityY(),
                "restore must retain current horizontal velocity and saved vertical velocity");

        PlayerState grounded = new PlayerState(1, 500, 480);
        PlayerState groundedSaved = grounded.copy();
        physics.applyCommand(player, PlayerCommand.RIGHT);
        player.restoreFrom(groundedSaved);
        checkState(player, RunningState.class);
        PlayerState runningSaved = player.copy();
        physics.applyCommand(player, PlayerCommand.STOP);
        player.restoreFrom(runningSaved);
        checkState(player, IdleState.class);
        physics.applyCommand(player, PlayerCommand.JUMP);
        checkState(player, RisingState.class);

        physics.placeAtStart(plains, player, other);
        checkState(player, FallingState.class);
        physics.update(player);
        checkState(player, IdleState.class);
        check(player.getVelocityX() == 0, "placement must stop horizontal movement");
    }

    private static void checkState(PlayerState player, Class<?> expected) {
        check(expected.isInstance(player.getMovementState()),
                "expected " + expected.getSimpleName() + ", got "
                        + player.getMovementState().getClass().getSimpleName());
    }

    private static void land(PlayerPhysics physics, PlayerState player) {
        for (int i = 0; i < 100 && !player.isOnGround(); i++) {
            physics.update(player);
        }
        check(player.isOnGround(), "player did not land");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
