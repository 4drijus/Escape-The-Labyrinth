package lt.escape.labyrinth.server.enemy;

import lt.escape.labyrinth.shared.EnemyType;

public class InstaDeathTurret extends Turret {

    public InstaDeathTurret(double x, double y, int firingIntervalMs) {
        super(x, y, firingIntervalMs);
    }

    @Override
    protected Bullet createBullet(double x, double y, double directionX, double directionY) {
        return new InstaDeathBullet(x, y, directionX, directionY);
    }

    @Override
    public EnemyType getType() {
        return EnemyType.INSTA_DEATH_TURRET;
    }
}
