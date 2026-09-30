package lt.escape.labyrinth.server.enemy;

import lt.escape.labyrinth.shared.BulletType;
import lt.escape.labyrinth.shared.PlayerState;

public class InstaDeathBullet extends Bullet {

    public InstaDeathBullet(double x, double y, double velocityX, double velocityY) {
        super(x, y, velocityX, velocityY);
    }

    @Override
    public void onHit(PlayerState player) {
        player.setHealth(0);
    }

    @Override
    public BulletType getType() {
        return BulletType.INSTA_DEATH;
    }
}
