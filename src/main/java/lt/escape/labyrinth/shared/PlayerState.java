package lt.escape.labyrinth.shared;

public class PlayerState {
    private final int playerId;

    private double x;
    private double y;
    private double velocityX;
    private double velocityY;

    private boolean onGround;

    private int health;

    private long invulnerableUntil;

    private static final int STARTING_HEALTH = 3;
    private static final long INVULNERABILITY_MS = 1000;

    public PlayerState(int playerId, double x, double y) {
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

}