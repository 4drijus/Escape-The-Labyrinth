package lt.escape.labyrinth.shared;

public class Player implements Prototype<Player> {
    private static final double MOVE_SPEED = 4;
    private static final double JUMP_VELOCITY = -8;

    private final int playerId;

    private double x;
    private double y;
    private double velocityX;
    private double velocityY;

    private boolean onGround;
    private MovementState movementState = IdleState.INSTANCE;
    private final StateManager stateManager = StateManager.INSTANCE;

    private int health;

    private long invulnerableUntil;

    private static final int STARTING_HEALTH = 3;
    private static final long INVULNERABILITY_MS = 1000;

    public Player(int playerId, double x, double y) {
        this.playerId = playerId;
        this.x = x;
        this.y = y;
        this.onGround = true;
        this.health = STARTING_HEALTH;
    }

    public int getPlayerId() {
        return playerId;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getVelocityX() {
        return velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public synchronized MovementState getMovementState() {
        return movementState;
    }

    public synchronized void applyMovementCommand(PlayerCommand command) {
        if (!movementState.isAllowed(command)) {
            return;
        }

        switch (command) {
            case LEFT -> velocityX = -MOVE_SPEED;
            case RIGHT -> velocityX = MOVE_SPEED;
            case STOP -> velocityX = 0;
            case JUMP -> {
                velocityY = JUMP_VELOCITY;
                onGround = false;
            }
            case SAVE, LOAD -> {
                return;
            }
        }
        updateMovementState();
    }

    public synchronized void updateMovementState() {
        movementState = stateManager.nextState(this);
    }

    public int getHealth() {
        return health;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setVelocityX(double velocityX) {
        this.velocityX = velocityX;
    }

    public void setVelocityY(double velocityY) {
        this.velocityY = velocityY;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    /** Ignored while the player is still invulnerable from a previous hit. */
    public void takeDamage(int amount) {
        long now = System.currentTimeMillis();
        if (now < invulnerableUntil) {
            return;
        }
        this.health = Math.max(0, this.health - amount);
        this.invulnerableUntil = now + INVULNERABILITY_MS;
    }

    public void resetHealth() {
        this.health = STARTING_HEALTH;
        this.invulnerableUntil = 0;
    }

    public boolean isAlive() {
        return health > 0;
    }

    @Override
    public synchronized Player copy() {
        Player copy = new Player(playerId, x, y);
        copy.velocityX = velocityX;
        copy.velocityY = velocityY;
        copy.onGround = onGround;
        copy.movementState = movementState;
        copy.health = health;
        copy.invulnerableUntil = invulnerableUntil;
        return copy;
    }

    /**
     * Loads a checkpoint into this player. velocityX is kept: it follows the
     * keys held right now, not the ones held when the checkpoint was saved.
     */
    public synchronized void restoreFrom(Player saved) {
        this.x = saved.x;
        this.y = saved.y;
        this.velocityY = saved.velocityY;
        this.onGround = saved.onGround;
        this.movementState = saved.movementState;
        this.health = saved.health;
        this.invulnerableUntil = saved.invulnerableUntil;
        updateMovementState();
    }

}