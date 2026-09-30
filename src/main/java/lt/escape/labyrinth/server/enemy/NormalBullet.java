package lt.escape.labyrinth.server.enemy;

import lt.escape.labyrinth.shared.BulletType;
import lt.escape.labyrinth.shared.PlayerState;

public class NormalBullet extends Bullet {

    private static final double SPEED = 300.0;

    private final int damage;

    public NormalBullet(double x, double y, double directionX, double directionY, int damage) {
        super(x, y, directionX, directionY, SPEED);
        this.damage = damage;
    }

    @Override
    public void onHit(PlayerState player) {
        player.takeDamage(damage);
    }

    @Override
    public BulletType getType() {
        return BulletType.NORMAL;
    }
}
