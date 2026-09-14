package lt.escape.labyrinth.client;

public class Player {
    private double x;
    private double y;

    private double velocityX;
    private double velocityY;

    private boolean onGround;

    public Player(double x, double y) {
        this.x = x;
        this.y = y;
        this.onGround = true;
    }

    public void moveLeft() {
        velocityX = -2;
    }

    public void moveRight() {
        velocityX = 2;
    }

    public void stopHorizontalMovement() {
        velocityX = 0;
    }

    public void jump() {
        if (onGround) {
            velocityY = -8;
            onGround = false;
        }
    }

    /**
     * Updates player position based on their velocity, applies gravity
     * Checks if player has reached the ground and makes sure it doesnt fall through
     */
    public void update() {
        x += velocityX;
        y += velocityY;

        // Gravity
        velocityY += 0.3;

        // Ground collision
        if (y >= 480) {
            y = 480;
            velocityY = 0;
            onGround = true;
        }
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
