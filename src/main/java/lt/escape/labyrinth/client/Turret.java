package lt.escape.labyrinth.client;

public abstract class Turret {
    protected final double positionX;
    protected final double positionY;

    protected Turret(double positionX, double positionY) {
        this.positionX = positionX;
        this.positionY = positionY;
    }

    protected abstract Bullet createBullet();

    public Bullet fire() {
        return createBullet();
    }
}
