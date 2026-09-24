package lt.escape.labyrinth.client;

public class InstaDeathTurret extends Turret {
    public InstaDeathTurret(double positionX, double positionY) {
        super(positionX, positionY);
    }

    @Override
    protected Bullet createBullet() {
        return new InstaDeathBullet();
    }
}
