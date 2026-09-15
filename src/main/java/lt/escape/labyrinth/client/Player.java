/*
Not used, but keeping it won't hurt for now
 */

/*
package lt.escape.labyrinth.client;

public class Player {
    private final int id;

    private double x;
    private double y;

    private double velocityX;
    private double velocityY;

    private boolean onGround;

    public Player(int id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.onGround = true;
    }

    public int getId(){
        return id;
    }

    public void moveLeft() {
        velocityX = -4;
    }

    public void moveRight() {
        velocityX = 4;
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

    public void update() {
        x += velocityX;
        y += velocityY;

        // Gravity
        velocityY += 0.5;

        // Ground collision
        if (y >= 480) {
            y = 480;
            velocityY = 0;
            onGround = true;
        }

        if (x < 0) {
            x = 0;
        }

        if (x > 945) {
            x = 945;
        }
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
}
*/