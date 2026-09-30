package lt.escape.labyrinth.server.enemy;

import lt.escape.labyrinth.shared.EnemyType;

public class NormalTurret extends Turret {

    private final int damage;

    public NormalTurret(double x, double y, int damage, int firingIntervalMs) {
        super(x, y, firingIntervalMs);
        this.damage = damage;
    }

    @Override
    protected Bullet createBullet(double x, double y, double directionX, double directionY) {
        return new NormalBullet(x, y, directionX, directionY, damage);
    }

    @Override
    public EnemyType getType() {
        return EnemyType.TURRET;
    }
}
