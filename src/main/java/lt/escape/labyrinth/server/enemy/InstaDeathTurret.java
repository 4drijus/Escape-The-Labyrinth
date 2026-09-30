package lt.escape.labyrinth.server.enemy;

import lt.escape.labyrinth.shared.EnemyType;

public class InstaDeathTurret extends Turret {

    public InstaDeathTurret(double x, double y, int firingIntervalMs) {
        this(x, y, firingIntervalMs, DEFAULT_BULLET_SPEED);
    }

    public InstaDeathTurret(double x, double y, int firingIntervalMs, double bulletSpeed) {
        super(x, y, firingIntervalMs, bulletSpeed);
    }

    @Override
    protected Bullet createBullet(double x, double y, double velocityX, double velocityY) {
        return new InstaDeathBullet(x, y, velocityX, velocityY);
    }

    @Override
    public EnemyType getType() {
        return EnemyType.INSTA_DEATH_TURRET;
    }
}
