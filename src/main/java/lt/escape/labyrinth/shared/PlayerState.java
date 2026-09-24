package lt.escape.labyrinth.shared;

public class PlayerState {
    private final int playerId;

    private double x;
    private double y;
    private double velocityX;
    private double velocityY;

    private boolean onGround;

    private int health;

    private static final int STARTING_HEALTH = 3;

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

    public void takeDamage(int amount) {
        this.health = Math.max(0, this.health - amount);
    }

    public boolean isAlive() {
        return health > 0;
    }

}