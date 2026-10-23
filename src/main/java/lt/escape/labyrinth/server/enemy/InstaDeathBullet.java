package lt.escape.labyrinth.server.enemy;

import lt.escape.labyrinth.shared.BulletType;
import lt.escape.labyrinth.shared.PlayerState;

public class InstaDeathBullet extends Bullet {

    private static final double SPEED = 300.0;

    public InstaDeathBullet(double x, double y, double directionX, double directionY) {
        super(x, y, directionX, directionY, SPEED);
    }

    private InstaDeathBullet(InstaDeathBullet source) {
        super(source);
    }

    @Override
    public InstaDeathBullet copy() {
        return new InstaDeathBullet(this);
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
