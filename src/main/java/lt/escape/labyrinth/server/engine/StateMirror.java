package lt.escape.labyrinth.server.engine;

import lt.escape.labyrinth.server.enemy.Bullet;
import lt.escape.labyrinth.server.enemy.Enemy;
import lt.escape.labyrinth.server.interactive.Door;
import lt.escape.labyrinth.server.interactive.Lever;
import lt.escape.labyrinth.server.level.Level;
import lt.escape.labyrinth.server.world.BlackPuddle;
import lt.escape.labyrinth.server.world.BluePuddle;
import lt.escape.labyrinth.server.world.Bush;
import lt.escape.labyrinth.server.world.Lift;
import lt.escape.labyrinth.server.world.RedPuddle;
import lt.escape.labyrinth.server.world.Tile;
import lt.escape.labyrinth.server.world.Wall;
import lt.escape.labyrinth.shared.BulletState;
import lt.escape.labyrinth.shared.ButtonState;
import lt.escape.labyrinth.shared.DoorState;
import lt.escape.labyrinth.shared.EnemyState;
import lt.escape.labyrinth.shared.ExitState;
import lt.escape.labyrinth.shared.GameState;
import lt.escape.labyrinth.shared.LeverState;
import lt.escape.labyrinth.shared.LiftState;
import lt.escape.labyrinth.shared.PressurePlateState;
import lt.escape.labyrinth.shared.TileState;
import lt.escape.labyrinth.shared.TileType;
import lt.escape.labyrinth.shared.WallState;

import java.util.ArrayList;
import java.util.List;

/**
 * SUBSYSTEM: publishes what the clients need to see. Copies the server-only
 * objects (Level, Enemy, Bullet, Door ...) into the plain shared snapshots
 * in GameState -- once per level for the static layout, and every tick for
 * whatever moves.
 */
final class StateMirror {

    private final GameState state;
    private List<Lift> lifts = List.of();
    private Lever lever;

    StateMirror(GameState state) {
        this.state = state;
    }

    /** The static layout of a freshly loaded level, plus the enemy list the per-tick sync will update. */
    void publishLevel(Level level, List<Enemy> enemies) {
        List<WallState> walls = new ArrayList<>();
        for (Wall wall : level.getWalls()) {
            walls.add(new WallState(wall.getPositionX(), wall.getPositionY(), wall.getWidth(), wall.getHeight()));
        }
        state.setWalls(walls);

        List<TileState> tiles = new ArrayList<>();
        for (Tile tile : level.getTiles()) {
            tiles.add(new TileState(tileTypeOf(tile), tile.getPositionX(), tile.getPositionY(), tile.getWidth(), tile.getHeight()));
        }
        state.setTiles(tiles);

        Door door = level.getDoor();
        state.setDoor(door == null ? null
                : new DoorState(door.getX(), door.getY(), door.getWidth(), door.getHeight(), door.isOpen()));
        var button = level.getButton();
        state.setButton(button == null ? null
                : new ButtonState(button.getX(), button.getY(), button.getWidth(), button.getHeight()));
        var plate = level.getPressurePlate();
        state.setPressurePlate(plate == null ? null
                : new PressurePlateState(plate.getX(), plate.getY(), plate.getWidth(), plate.getHeight()));
        var exit = level.getExit();
        state.setExit(exit == null ? null
                : new ExitState(exit.getX(), exit.getY(), exit.getWidth(), exit.getHeight()));

        // Lifts and the lever change during play, so keep them for sync().
        lifts = level.getLifts();
        List<LiftState> liftStates = new ArrayList<>();
        for (Lift lift : lifts) {
            liftStates.add(new LiftState(lift.getPositionX(), lift.getPositionY(), lift.getWidth(), lift.getHeight()));
        }
        state.setLifts(liftStates);
        lever = level.getLever();
        state.setLever(lever == null ? null
                : new LeverState(lever.getX(), lever.getY(), lever.getWidth(), lever.getHeight(), lever.getState()));

        state.getEnemies().clear();
        for (Enemy enemy : enemies) {
            state.getEnemies().add(new EnemyState(enemy.getType(), enemy.getX(), enemy.getY()));
        }
        state.setBullets(new ArrayList<>());
    }

    /** Called every tick: enemy positions, the live bullets, and whether the door is open. */
    void sync(List<Enemy> enemies, List<Bullet> bullets, Door door) {
        List<EnemyState> enemyStates = state.getEnemies();
        for (int i = 0; i < enemies.size(); i++) {
            enemyStates.get(i).setX(enemies.get(i).getX());
            enemyStates.get(i).setY(enemies.get(i).getY());
        }

        List<BulletState> bulletStates = new ArrayList<>();
        for (Bullet bullet : bullets) {
            bulletStates.add(new BulletState(bullet.getType(), bullet.getX(), bullet.getY()));
        }
        state.setBullets(bulletStates);

        if (door != null && state.getDoor() != null) {
            state.getDoor().setOpen(door.isOpen());
        }

        List<LiftState> liftStates = state.getLifts();
        for (int i = 0; i < lifts.size(); i++) {
            liftStates.get(i).setY(lifts.get(i).getPositionY());
        }
        if (lever != null && state.getLever() != null) {
            state.getLever().setOn(lever.getState());
        }
    }

    private TileType tileTypeOf(Tile tile) {
        if (tile instanceof BluePuddle) {
            return TileType.BLUE_PUDDLE;
        }
        if (tile instanceof RedPuddle) {
            return TileType.RED_PUDDLE;
        }
        if (tile instanceof BlackPuddle) {
            return TileType.BLACK_PUDDLE;
        }
        if (tile instanceof Bush) {
            return TileType.BUSH;
        }
        throw new IllegalArgumentException("Unknown tile type: " + tile.getClass());
    }
}