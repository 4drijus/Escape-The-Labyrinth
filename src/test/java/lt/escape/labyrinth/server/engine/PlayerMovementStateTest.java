package lt.escape.labyrinth.server.engine;

import lt.escape.labyrinth.server.interactive.NormalElementFactory;
import lt.escape.labyrinth.server.level.EasyLevelBuilder;
import lt.escape.labyrinth.server.level.Level;
import lt.escape.labyrinth.server.level.LevelLayout;
import lt.escape.labyrinth.shared.FallingState;
import lt.escape.labyrinth.shared.IdleState;
import lt.escape.labyrinth.shared.MovementState;
import lt.escape.labyrinth.shared.PlayerCommand;
import lt.escape.labyrinth.shared.Player;
import lt.escape.labyrinth.shared.RisingState;
import lt.escape.labyrinth.shared.RunningState;

public class PlayerMovementStateTest {
    public static void main(String[] args) {
        testStatePermissions();
        testJumpAndLanding();
        testWalkingOffEdge();
        testCeilingCollision();
        testCheckpointAndPlacement();
        System.out.println("All movement state tests passed.");
    }

    private static Level level(LevelLayout layout) {
        return new EasyLevelBuilder(new NormalElementFactory(), layout).buildLevel();
    }

    private static void testStatePermissions() {
        Player player = new Player(1, 500, 480);
        MovementState groundedState = player.getMovementState();
        check(groundedState.isAllowed(PlayerCommand.LEFT), "grounded left command");
        check(groundedState.isAllowed(PlayerCommand.RIGHT), "grounded right command");
        check(groundedState.isAllowed(PlayerCommand.STOP), "grounded stop command");
        check(groundedState.isAllowed(PlayerCommand.JUMP), "grounded jump command");
        check(!groundedState.isAllowed(PlayerCommand.SAVE), "save is not a movement command");
        check(!groundedState.isAllowed(PlayerCommand.LOAD), "load is not a movement command");

        player.applyMovementCommand(PlayerCommand.JUMP);
        MovementState airborneState = player.getMovementState();
        check(airborneState.isAllowed(PlayerCommand.LEFT), "airborne horizontal command");
        check(airborneState.isAllowed(PlayerCommand.STOP), "airborne stop command");
        check(!airborneState.isAllowed(PlayerCommand.JUMP), "airborne jump command");
    }

    private static void testJumpAndLanding() {
        PlayerPhysics physics = new PlayerPhysics();
        physics.load(level(LevelLayout.plains()));
        Player player = new Player(1, 500, 480);
        Player other = new Player(2, 100, 480);
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
        Player player = new Player(1, 358, 430);
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
        Player player = new Player(1, 200, 480);
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
        Player player = new Player(1, 500, 480);
        Player other = new Player(2, 100, 480);
        physics.applyCommand(player, PlayerCommand.RIGHT);
        physics.applyCommand(player, PlayerCommand.JUMP);
        physics.update(player);
        Player saved = player.copy();
        checkState(saved, RisingState.class);
        physics.applyCommand(player, PlayerCommand.STOP);
        player.restoreFrom(saved);
        checkState(player, RisingState.class);
        check(player.getVelocityX() == 0 && player.getVelocityY() == saved.getVelocityY(),
                "restore must retain current horizontal velocity and saved vertical velocity");

        Player grounded = new Player(1, 500, 480);
        Player groundedSaved = grounded.copy();
        physics.applyCommand(player, PlayerCommand.RIGHT);
        player.restoreFrom(groundedSaved);
        checkState(player, RunningState.class);
        Player runningSaved = player.copy();
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

    private static void checkState(Player player, Class<?> expected) {
        check(expected.isInstance(player.getMovementState()),
                "expected " + expected.getSimpleName() + ", got "
                        + player.getMovementState().getClass().getSimpleName());
    }

    private static void land(PlayerPhysics physics, Player player) {
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
