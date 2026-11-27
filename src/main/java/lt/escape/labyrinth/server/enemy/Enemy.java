package lt.escape.labyrinth.server.enemy;

import lt.escape.labyrinth.server.GameConstants;
import lt.escape.labyrinth.server.world.GameObject;
import lt.escape.labyrinth.server.world.Wall;
import lt.escape.labyrinth.shared.EnemyType;
import lt.escape.labyrinth.shared.Player;
import lt.escape.labyrinth.shared.Prototype;

import java.util.Collections;
import java.util.List;

/**
 * Context class in the Strategy pattern. Holds a Behavior and delegates
 * movement to it every tick.
 *
 * The behavior can be swapped while the game is running:
 *  - directly, with setBehavior(), or
 *  - automatically, with setBehaviorRotation(): every N seconds the enemy
 *    switches to the next strategy in a list, looping forever.
 *
 * Each concrete enemy implements copy()
 *
 * A GameObject like everything else in a level, so the level's iterator
 * includes enemies. Its box is ENEMY_SIZE square, the size wouldHitWall() uses.
 */
public abstract class Enemy extends GameObject implements Prototype<Enemy> {

    private Behavior behavior;

    // Optional runtime switching (see setBehaviorRotation).
    private List<Behavior> rotation = List.of();
    private double rotationIntervalSeconds;
    private double rotationTimer;
    private int rotationIndex;

    protected Enemy(double x, double y, Behavior behavior) {
        super(x, y, GameConstants.ENEMY_SIZE, GameConstants.ENEMY_SIZE);
        this.behavior = behavior;
    }

    /**
     * Copy constructor for copy(). The behaviors are copied too, not shared,
     * and the copy carries on from the same point in its rotation.
     */
    protected Enemy(Enemy source) {
        super(source.positionX, source.positionY, source.width, source.height);
        this.rotation = source.rotation.stream().map(Behavior::copy).toList();
        this.rotationIntervalSeconds = source.rotationIntervalSeconds;
        this.rotationTimer = source.rotationTimer;
        this.rotationIndex = source.rotationIndex;

        // The active behavior is normally the rotation's current one -- point at
        // that copy instead of making a second, separate copy of it.
        boolean activeIsInRotation = !source.rotation.isEmpty()
                && source.behavior == source.rotation.get(source.rotationIndex);
        if (activeIsInRotation) {
            this.behavior = rotation.get(rotationIndex);
        } else {
            this.behavior = source.behavior == null ? null : source.behavior.copy();
        }
    }

    public abstract EnemyType getType();

    /** Swaps the active strategy. The new one is reset so it anchors at the enemy's current position. */
    public void setBehavior(Behavior behavior) {
        if (behavior != null) {
            behavior.reset();
        }
        this.behavior = behavior;
    }

    /**
     * Makes this enemy cycle through the given strategies, switching every
     * intervalSeconds, starting with the first one.
     */
    public void setBehaviorRotation(double intervalSeconds, Behavior... behaviors) {
        if (behaviors.length == 0 || intervalSeconds <= 0) {
            throw new IllegalArgumentException("Need at least one behavior and a positive interval");
        }
        this.rotation = List.of(behaviors);
        this.rotationIntervalSeconds = intervalSeconds;
        this.rotationTimer = 0;
        this.rotationIndex = 0;
        setBehavior(rotation.get(0));
    }

    /**
     * Called once per game tick by GameServer.
     * Returns any bullets spawned this tick (empty for enemies that don't shoot).
     */
    public List<Bullet> update(List<Player> players, List<Wall> walls, double deltaTime) {
        advanceRotation(deltaTime);

        if (behavior != null) {
            behavior.move(this, players, walls, deltaTime);
        }
        return Collections.emptyList();
    }

    private void advanceRotation(double deltaTime) {
        if (rotation.isEmpty()) {
            return;
        }

        rotationTimer += deltaTime;
        if (rotationTimer >= rotationIntervalSeconds) {
            rotationTimer = 0;
            rotationIndex = (rotationIndex + 1) % rotation.size();
            Behavior next = rotation.get(rotationIndex);
            setBehavior(next);
            System.out.println(getType() + " switched to " + next.getClass().getSimpleName());
        }
    }

    /** True if this enemy, moved to (newX, newY), would overlap any wall. */
    public boolean wouldHitWall(double newX, double newY, List<Wall> walls) {
        if (walls == null) {
            return false;
        }
        for (Wall wall : walls) {
            if (wall.intersects(newX, newY, GameConstants.ENEMY_SIZE, GameConstants.ENEMY_SIZE)) {
                return true;
            }
        }
        return false;
    }

    public void setX(double x) {
        this.positionX = x;
    }

    public void setY(double y) {
        this.positionY = y;
    }
}