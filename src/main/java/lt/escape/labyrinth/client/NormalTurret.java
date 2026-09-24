package lt.escape.labyrinth.client;

public class NormalTurret extends Turret {
    public NormalTurret(double positionX, double positionY) {
        super(positionX, positionY);
    }

    @Override
    protected Bullet createBullet() {
        return new NormalBullet();
    }
}
