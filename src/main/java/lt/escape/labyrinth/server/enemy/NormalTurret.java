package lt.escape.labyrinth.server.enemy;

import lt.escape.labyrinth.shared.EnemyType;

public class NormalTurret extends Turret {

    private final int damage;

    public NormalTurret(double x, double y, int damage, int firingIntervalMs) {
        this(x, y, damage, firingIntervalMs, DEFAULT_BULLET_SPEED);
    }

    public NormalTurret(double x, double y, int damage, int firingIntervalMs, double bulletSpeed) {
        super(x, y, firingIntervalMs, bulletSpeed);
        this.damage = damage;
    }

    @Override
    protected Bullet createBullet(double x, double y, double velocityX, double velocityY) {
        return new NormalBullet(x, y, velocityX, velocityY, damage);
    }

    @Override
    public EnemyType getType() {
        return EnemyType.TURRET;
    }
}
