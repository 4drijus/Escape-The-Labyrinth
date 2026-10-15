package lt.escape.labyrinth.server.enemy;

import lt.escape.labyrinth.server.world.Wall;
import lt.escape.labyrinth.shared.EnemyType;
import lt.escape.labyrinth.shared.PlayerState;
import lt.escape.labyrinth.shared.Prototype;

import java.util.Collections;
import java.util.List;

/**
 * Context class in the Strategy pattern. Holds a Behavior and delegates
 * movement decisions to it every tick.
 *
 * update() now also takes the level's walls and passes them straight
 * through to the Behavior -- Enemy itself doesn't do any wall logic,
 * it just plumbs the data through to whichever strategy needs it.
 *
 * Also a Prototype: each concrete enemy implements copy() through a copy
 * constructor that ends up in Enemy(Enemy) below.
 */
public abstract class Enemy implements Prototype<Enemy> {

    protected double x;
    protected double y;
    private Behavior behavior;

    protected Enemy(double x, double y, Behavior behavior) {
        this.x = x;
        this.y = y;
        this.behavior = behavior;
    }

    /** Copy constructor for copy(). The Behavior is copied too, not shared. */
    protected Enemy(Enemy source) {
        this.x = source.x;
        this.y = source.y;
        this.behavior = source.behavior == null ? null : source.behavior.copy();
    }

    public void setBehavior(Behavior behavior) {
        this.behavior = behavior;
    }

    /**
     * This was missing entirely -- GameServer.initEnemyStates() calls
     * enemy.getType() through a List<Enemy>, so the base class needs to
     * declare it even though every concrete subclass (SpikyBush,
     * NormalTurret, InstaDeathTurret) already implements it.
     */
    public abstract EnemyType getType();

    /**
     * Called once per game tick by GameServer's update loop.
     * Returns any bullets this enemy spawned this tick (empty for
     * enemies, like SpikyBush, that don't shoot).
     */
    public List<Bullet> update(List<PlayerState> players, List<Wall> walls, double deltaTime) {
        if (behavior != null) {
            behavior.move(this, players, walls, deltaTime);
        }
        return Collections.emptyList();
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