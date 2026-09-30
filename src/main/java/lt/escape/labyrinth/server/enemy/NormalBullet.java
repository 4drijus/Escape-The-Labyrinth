package lt.escape.labyrinth.server.enemy;

import lt.escape.labyrinth.shared.BulletType;
import lt.escape.labyrinth.shared.PlayerState;

public class NormalBullet extends Bullet {

    private final int damage;

    public NormalBullet(double x, double y, double velocityX, double velocityY, int damage) {
        super(x, y, velocityX, velocityY);
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
