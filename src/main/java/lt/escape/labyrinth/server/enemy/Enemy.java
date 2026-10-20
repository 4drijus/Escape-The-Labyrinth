package lt.escape.labyrinth.server.enemy;

import lt.escape.labyrinth.server.GameConstants;
import lt.escape.labyrinth.server.world.Wall;
import lt.escape.labyrinth.shared.EnemyType;
import lt.escape.labyrinth.shared.PlayerState;

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
 */
public abstract class Enemy {

    protected double x;
    protected double y;
    private Behavior behavior;

    // Optional runtime switching (see setBehaviorRotation).
    private List<Behavior> rotation = List.of();
    private double rotationIntervalSeconds;
    private double rotationTimer;
    private int rotationIndex;

    protected Enemy(double x, double y, Behavior behavior) {
        this.x = x;
        this.y = y;
        this.behavior = behavior;
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
    public List<Bullet> update(List<PlayerState> players, List<Wall> walls, double deltaTime) {
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

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}