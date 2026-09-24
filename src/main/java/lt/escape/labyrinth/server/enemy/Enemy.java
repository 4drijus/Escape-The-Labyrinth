package lt.escape.labyrinth.server.enemy;

import lt.escape.labyrinth.shared.PlayerState;

import java.util.Collections;
import java.util.List;

/**
 * Context class in the Strategy pattern. Holds a Behavior and delegates
 * movement decisions to it every tick, instead of hard-coding movement
 * logic in each Enemy subclass.
 */
public abstract class Enemy {

    protected double x;
    protected double y;
    private Behavior behavior;

    protected Enemy(double x, double y, Behavior behavior) {
        this.x = x;
        this.y = y;
        this.behavior = behavior;
    }

    /**
     * Lets an enemy change strategy at runtime, e.g. patrol until it
     * spots a player, then switch to StationaryBehavior (or a future
     * ChaseBehavior) to attack.
     */
    public void setBehavior(Behavior behavior) {
        this.behavior = behavior;
    }

    /** Called once per game tick by GameServer's update loop. */
    public List<Bullet> update(List<PlayerState> players, double deltaTime) {
        if (behavior != null) {
            behavior.move(this, players, deltaTime);
        }
        return Collections.emptyList();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
}